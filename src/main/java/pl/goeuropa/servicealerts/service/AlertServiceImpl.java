package pl.goeuropa.servicealerts.service;

import com.google.transit.realtime.GtfsRealtime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.goeuropa.servicealerts.repository.AlertRepository;
import pl.goeuropa.servicealerts.model.ServiceAlert;
import pl.goeuropa.servicealerts.scheduler.BackupListScheduler;
import pl.goeuropa.servicealerts.scheduler.RefreshProtoFileScheduler;
import pl.goeuropa.servicealerts.utils.AlertBuilderUtil;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository = AlertRepository.getInstance();

    private final BackupListScheduler backupList;
    private final RefreshProtoFileScheduler rewriteFile;

    @Value("${alert-api.zone}")
    private String zoneId;

    @Override
    public void createAlert(ServiceAlert newAlert) {
        long time = getDateTimeNow();
        newAlert.setId(String.valueOf(time * 1000));
        newAlert.setCreationTime(time);
        try {
            alertRepository.addToAlertList(newAlert);
            backupList.saveAlertsToFile();
            rewriteFile.updateServiceAlertsProtoBufFile();
        log.info("- Added new alert {} and save to back-up list", newAlert);
        } catch (Exception e) {
            log.error("Alert: {} - doesn't save : {} ", newAlert, e.getMessage());
        }
    }

    @Override
    public GtfsRealtime.FeedMessage getAlertsByAgency(String agencyId) throws RuntimeException {
        List<ServiceAlert> unfilteredListOfAlerts;
        List<ServiceAlert> filteredListOfAlerts;
        if (!alertRepository.getServiceAlertList().isEmpty()) {
            unfilteredListOfAlerts = alertRepository.getServiceAlertList();
            filteredListOfAlerts = unfilteredListOfAlerts
                    .stream()
                    .filter(alert -> alert.getAgencyId().equals(agencyId)
                    )
                    .collect(Collectors.toList());

            GtfsRealtime.FeedMessage.Builder feed = GtfsRealtime.FeedMessage.newBuilder();
            GtfsRealtime.FeedHeader.Builder header = feed.getHeaderBuilder();
            header.setGtfsRealtimeVersion("2.0");
            header.setTimestamp(getDateTimeNow() / 1000);


            if (filteredListOfAlerts.isEmpty())
                throw new IllegalStateException(String.format("List of alerts is empty for agencyId: %s", agencyId));

            AlertBuilderUtil.fillFeedMessage(feed, filteredListOfAlerts, zoneId);
            log.debug("Got {} service-alerts for agencyId {} ", filteredListOfAlerts.size(), agencyId);
            return feed.build();
        }
        throw new IllegalStateException("List of alerts is empty");
    }

    @Override
    public GtfsRealtime.FeedMessage getAlerts() throws RuntimeException {
        List<ServiceAlert> listOfAlerts;
        if (!alertRepository.getServiceAlertList().isEmpty()) {
            listOfAlerts = alertRepository.getServiceAlertList();

            GtfsRealtime.FeedMessage.Builder feed = GtfsRealtime.FeedMessage.newBuilder();
            GtfsRealtime.FeedHeader.Builder header = feed.getHeaderBuilder();
            header.setGtfsRealtimeVersion("2.0");
            header.setTimestamp(getDateTimeNow() / 1000);
            List<ServiceAlert> sortedListOfAlerts = listOfAlerts.stream()
                    .sorted(Comparator.comparingLong(ServiceAlert::getCreationTime))
                    .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
            AlertBuilderUtil.fillFeedMessage(feed, sortedListOfAlerts, zoneId);
            return feed.build();
        }
        throw new IllegalStateException("List of alerts is empty");
    }

    @Override
    public List<ServiceAlert> getAlertListByAgency(String agencyId) throws RuntimeException {
        List<ServiceAlert> filteredListOfAlerts;
        List<ServiceAlert> unfilteredListOfAlerts;
        if (!alertRepository.getServiceAlertList().isEmpty()) {
            unfilteredListOfAlerts = alertRepository.getServiceAlertList();
            filteredListOfAlerts = unfilteredListOfAlerts
                    .stream()
                    .filter(alert -> alert.getAgencyId().equals(agencyId)
                    )
                    .collect(Collectors.toList());
            if (filteredListOfAlerts.isEmpty()) {
                log.debug("List of alerts is empty for agencyId {} ", agencyId);
            }
            return filteredListOfAlerts;
        }
        throw new IllegalStateException("List of alerts is empty");
    }

    @Override
    public LinkedList<ServiceAlert> getAlertList() throws RuntimeException {
        if (alertRepository.getServiceAlertList().isEmpty())
            throw new IllegalStateException("List of alerts is empty");

        log.debug("Got sorted by creation time : {} service-alerts ", alertRepository.getServiceAlertList().size());
        return alertRepository.getServiceAlertList().stream()
                .sorted(Comparator.comparingLong(ServiceAlert::getCreationTime))
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }

    @Override
    public void editAlert(String alertId, ServiceAlert updatedAlert) throws RuntimeException {
        Optional<ServiceAlert> existingAlert = alertRepository.getServiceAlertList()
                .stream()
                .filter(alert -> alert.getId().equals(alertId))
                .findFirst();
        if (existingAlert.isPresent() && updatedAlert.getId().equals(alertId)) {
            int index = alertRepository.getServiceAlertList().indexOf(existingAlert.get());
            if (index != -1) {
                alertRepository.getServiceAlertList().set(index, updatedAlert);
            }
        if (alertRepository.getServiceAlertList().contains(updatedAlert)) {
        log.debug("Alert with id : {} - successfully updated", updatedAlert.getId());
        } else {
            log.warn("Alert with id : {} - does not exist", updatedAlert.getId());
            throw new IllegalStateException(String.format("Alert with id : %s - does not exist", updatedAlert.getId()));
        }} else {
            log.warn("Alert with id : {} - does not exist", updatedAlert.getId());
            throw new IllegalStateException(String.format("Alert with id : %s - does not exist", updatedAlert.getId()));
        }
    }


    @Override
    public void deleteAlertById(String id) throws RuntimeException {
        alertRepository.getServiceAlertList()
                .removeIf(alert -> alert.getId()
                        .equals(id));
        if (alertRepository.getServiceAlertList()
                .stream()
                .anyMatch(alert -> alert.getId().equals(id))) {
            log.warn("Something get wrong. The alert still is.");
            throw new IllegalStateException("Something get wrong. The alert still is. Try again");
        }
        log.debug("Alert with id: {} - successfully deleted", id);
    }

    @Override
    public void deleteAlertsByAgency(String agencyId) throws RuntimeException {
        alertRepository.getServiceAlertList()
                .removeIf(alert -> alert.getAgencyId()
                        .equals(agencyId));
        if (alertRepository.getServiceAlertList()
                .stream()
                .anyMatch(alert -> alert.getAgencyId().equals(agencyId))) {
            log.warn("Something get wrong. Alerts still are.");
            throw new IllegalStateException("Something get wrong. Alerts still are. Try again");
        }
        log.debug("Alerts for agencyId: {} - successfully deleted", agencyId);
    }

    @Override
    public void cleanAlertList() throws RuntimeException {
        alertRepository.clearServiceAlertsList();
        if (!alertRepository.getServiceAlertList().isEmpty()) {
            log.warn("Something get wrong. The alerts still in the list.");
            throw new IllegalStateException("Something get wrong. Please, try again");
        }
        log.debug("Alert list of service-alerts successful cleaned");
    }

    private long getDateTimeNow () {
        return now()
                .atZone(ZoneId.of(zoneId))
                .toEpochSecond();
    }
}
