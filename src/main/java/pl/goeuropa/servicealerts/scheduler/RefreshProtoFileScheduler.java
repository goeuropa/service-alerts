package pl.goeuropa.servicealerts.scheduler;

import com.google.transit.realtime.GtfsRealtime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.goeuropa.servicealerts.service.AlertService;

import java.io.FileOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshProtoFileScheduler {

    @Value("${alert-api.pb-file.out-path}")
    private String outputPath;

    private final AlertService alertService;

    @Scheduled(cron = "hourly")
    public void updateVehiclesPositionsProtoBufFile() {
        try (FileOutputStream toFile = new FileOutputStream(outputPath)) {

            GtfsRealtime.FeedMessage feed = alertService.getAlerts();
            log.info("Write to file: {} entities.", feed.getEntityList().size());

            //Writing to protobuf file
            feed.writeTo(toFile);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
