package pl.goeuropa.servicealerts.service;

import com.google.transit.realtime.GtfsRealtime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import pl.goeuropa.servicealerts.cache.CacheManager;
import pl.goeuropa.servicealerts.model.servicealerts.ServiceAlert;
import pl.goeuropa.servicealerts.utils.AlertBuilderUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Slf4j
@EnableScheduling
@Service
public class AlertServiceImpl implements AlertService {

    private final CacheManager cacheManager = CacheManager.getInstance();


    @Value("${alert-api.zone}")
    private String ZONE_ID;

    @Override
    public void createAlert(ServiceAlert newAlert) {
        long time = getDateTimeNow();
        newAlert.setId(String.valueOf(time * 1000));
        newAlert.setCreationTime(time);
        cacheManager.addToAlertList(newAlert);
        log.info("-- Added new one {}", newAlert);
    }

    @Override
    public GtfsRealtime.FeedMessage getAlertsByAgency(String agencyId) throws RuntimeException {
        List<ServiceAlert> unfilteredListOfAlerts;
        List<ServiceAlert> filteredListOfAlerts;
        if (!cacheManager.getServiceAlertsList().isEmpty()) {
            unfilteredListOfAlerts = cacheManager.getServiceAlertsList();
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

            AlertBuilderUtil.fillFeedMessage(feed, filteredListOfAlerts, ZONE_ID);
            log.info("-- Got {} service-alerts for agencyId {} ", filteredListOfAlerts.size(), agencyId);
            return feed.build();
        }
        throw new IllegalStateException("List of alerts is empty");
    }

    @Override
    public GtfsRealtime.FeedMessage getAlerts() throws RuntimeException {
        List<ServiceAlert> listOfAlerts;
        if (!cacheManager.getServiceAlertsList().isEmpty()) {
            listOfAlerts = cacheManager.getServiceAlertsList();

            GtfsRealtime.FeedMessage.Builder feed = GtfsRealtime.FeedMessage.newBuilder();
            GtfsRealtime.FeedHeader.Builder header = feed.getHeaderBuilder();
            header.setGtfsRealtimeVersion("2.0");
            header.setTimestamp(getDateTimeNow() / 1000);
            List<ServiceAlert> sortedListOfAlerts = listOfAlerts.stream()
                    .sorted(Comparator.comparingLong(ServiceAlert::getCreationTime))
                    .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
            AlertBuilderUtil.fillFeedMessage(feed, sortedListOfAlerts, ZONE_ID);
            return feed.build();
        }
        throw new IllegalStateException("List of alerts is empty");
    }

    @Override
    public List<ServiceAlert> getAlertListByAgency(String agencyId) throws RuntimeException {
        List<ServiceAlert> filteredListOfAlerts;
        List<ServiceAlert> unfilteredListOfAlerts;
        if (!cacheManager.getServiceAlertsList().isEmpty()) {
            unfilteredListOfAlerts = cacheManager.getServiceAlertsList();
            filteredListOfAlerts = unfilteredListOfAlerts
                    .stream()
                    .filter(alert -> alert.getAgencyId().equals(agencyId)
                    )
                    .collect(Collectors.toList());
            if (filteredListOfAlerts.isEmpty()) {
                log.info("-- List of alerts is empty for agencyId {} ", agencyId);
            }
            return filteredListOfAlerts;
        }
        throw new IllegalStateException("List of alerts is empty");
    }


    @Override
    public LinkedList<ServiceAlert> getAlertList() throws RuntimeException {
        if (cacheManager.getServiceAlertsList().isEmpty())
            throw new IllegalStateException("List of alerts is empty");

        log.info("-- Got sorted by creation time : {} service-alerts ", cacheManager.getServiceAlertsList().size());
        return cacheManager.getServiceAlertsList().stream()
                .sorted(Comparator.comparingLong(ServiceAlert::getCreationTime))
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }

    @Override
    public void editAlert(ServiceAlert updatedAlert) throws RuntimeException {
        cacheManager.getServiceAlertsList()
                .removeIf(alert -> alert.getId()
                        .equals(updatedAlert.getId()));
        cacheManager.getServiceAlertsList().add(updatedAlert);
        if (cacheManager.getServiceAlertsList().contains(updatedAlert)){
        log.info("-- Alert with id : {} - successfully updated", updatedAlert.getId());
        } else {
            log.error("-- Alert with id : {} - does not exist", updatedAlert.getId());
            throw new IllegalStateException(String.format("Alert with id : %s - does not exist", updatedAlert.getId()));
        }
    }


    @Override
    public void deleteAlertById(String id) throws RuntimeException {
        cacheManager.getServiceAlertsList()
                .removeIf(alert -> alert.getId()
                        .equals(id));
        if (cacheManager.getServiceAlertsList()
                .stream()
                .anyMatch(alert -> alert.getId().equals(id))) {
            log.debug("Something get wrong. The alert still is.");
            throw new IllegalStateException("Something get wrong. The alert still is. Try again");
        }
        log.info("-- Alert with id: {} - successfully deleted", id);
    }

    @Override
    public void deleteAlertsByAgency(String id) throws RuntimeException {
        cacheManager.getServiceAlertsList()
                .removeIf(alert -> alert.getAgencyId()
                        .equals(id));
        if (cacheManager.getServiceAlertsList()
                .stream()
                .anyMatch(alert -> alert.getAgencyId().equals(id))) {
            log.debug("Something get wrong. Alerts still are.");
            throw new IllegalStateException("Something get wrong. Alerts still are. Try again");
        }
        log.info("-- Alerts for agencyId: {} - successfully deleted", id);
    }

    @Override
    public void cleanAlertList() throws RuntimeException {
        cacheManager.clearServiceAlertsList();
        if (!cacheManager.getServiceAlertsList().isEmpty()) {
            log.debug("Something get wrong. The alerts still in the list.");
            throw new IllegalStateException("Something get wrong. Please, try again");
        }
        log.info("Cache list of service-alerts successful cleaned");
    }

    private long getDateTimeNow () {
        return now()
                .atZone(ZoneId.of(ZONE_ID))
                .toEpochSecond();
    }
}
