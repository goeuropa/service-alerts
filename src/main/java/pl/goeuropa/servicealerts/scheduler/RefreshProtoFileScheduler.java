package pl.goeuropa.servicealerts.scheduler;

import com.google.transit.realtime.GtfsRealtime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.goeuropa.servicealerts.model.servicealerts.ServiceAlert;
import pl.goeuropa.servicealerts.repository.AlertRepository;
import pl.goeuropa.servicealerts.service.AlertService;
import pl.goeuropa.servicealerts.utils.AlertBuilderUtil;

import java.io.FileOutputStream;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.time.Instant.now;

@Slf4j
@Component
public class RefreshProtoFileScheduler {

    @Value("${alert-api.pb-file.out-path}")
    private String outputPath;

    @Value("${alert-api.zone}")
    private String zoneId;

    private final AlertRepository alertRepository = AlertRepository.getInstance();

    @Scheduled(fixedDelay = 60_000)
    public void updateVehiclesPositionsProtoBufFile() {
        try (FileOutputStream toFile = new FileOutputStream(outputPath)) {

            GtfsRealtime.FeedMessage feed = getAlerts();
            log.debug("Write to file: {}, {} entities.", outputPath, feed.getEntityList().size());

            //Writing to protobuf file
            feed.writeTo(toFile);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private GtfsRealtime.FeedMessage getAlerts() {
        List<ServiceAlert> listOfAlerts;
        if (!alertRepository.getServiceAlertsList().isEmpty()) {
            listOfAlerts = alertRepository.getServiceAlertsList();

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

    private long getDateTimeNow () {
        return now()
                .atZone(ZoneId.of(zoneId))
                .toEpochSecond();
    }
}
