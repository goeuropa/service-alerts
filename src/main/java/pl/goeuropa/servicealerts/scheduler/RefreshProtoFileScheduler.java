package pl.goeuropa.servicealerts.scheduler;

import com.google.transit.realtime.GtfsRealtime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.goeuropa.servicealerts.model.ServiceAlert;
import pl.goeuropa.servicealerts.repository.AlertRepository;
import pl.goeuropa.servicealerts.utils.AlertBuilderUtil;

import java.io.FileOutputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Slf4j
@Component
public class RefreshProtoFileScheduler {

    @Value("${alert-api.pb-file.out-path}")
    private String outputPath;

    @Value("${alert-api.zone}")
    private String zoneId;

    private final AlertRepository alertRepository = AlertRepository.getInstance();

    @Scheduled(fixedRateString = "${alert-api.pb-file.interval}",
            timeUnit = TimeUnit.SECONDS)
    public void updateServiceAlertsProtoBufFile() {

        Set<String> agencyIds = alertRepository.getAgencyIds();

        for (String agencyId : agencyIds) {
            writeToFile(agencyId);
        }
    }

    private void writeToFile(String agencyId) {
        try (FileOutputStream toFile = new FileOutputStream(outputPath + agencyId)) {
            GtfsRealtime.FeedMessage feed = getAlertsByAgency(agencyId);
            log.debug("Write to file: {}, {} entities.", outputPath, feed.getEntityList().size());

            //Writing to protobuf file
            feed.writeTo(toFile);

        } catch (Exception ex) {
            if (ex instanceof IllegalStateException) {
                log.debug(ex.getMessage());
            } else {
                log.warn(ex.getMessage());
            }
        }
    }

    private GtfsRealtime.FeedMessage getAlertsByAgency(String agencyId) throws RuntimeException {
        List<ServiceAlert> unfilteredListOfAlerts;
        List<ServiceAlert> filteredListOfAlerts;
        if (!alertRepository.getServiceAlertList().isEmpty()) {
            unfilteredListOfAlerts = alertRepository.getServiceAlertList();
            filteredListOfAlerts = unfilteredListOfAlerts
                    .stream()
                    .filter(
                            alert -> alert.getActiveWindows().get(0).getLongTo(zoneId) >= getDateTimeNow()
                                    &&
                                    alert.getAgencyId().equals(agencyId)
                    )
                    .collect(Collectors.toList());

            GtfsRealtime.FeedMessage.Builder feed = GtfsRealtime.FeedMessage.newBuilder();
            GtfsRealtime.FeedHeader.Builder header = feed.getHeaderBuilder();
            header.setGtfsRealtimeVersion("2.0");
            header.setTimestamp(getDateTimeNow() / 1000);

            if (filteredListOfAlerts.isEmpty())
                log.debug("There aren't any actual alerts for agencyId: %s", agencyId);

            AlertBuilderUtil.fillFeedMessage(feed, filteredListOfAlerts, zoneId);
            log.debug("Got {} service-alerts for agencyId {} ", filteredListOfAlerts.size(), agencyId);
            return feed.build();
        }
        throw new IllegalStateException("List of alerts is empty");
    }

    private long getDateTimeNow() {
        return now()
                .atZone(ZoneId.of(zoneId))
                .toEpochSecond();
    }
}
