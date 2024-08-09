package pl.goeuropa.servicealerts.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.goeuropa.servicealerts.model.servicealerts.ServiceAlert;
import pl.goeuropa.servicealerts.repository.AlertRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
public class BackupListScheduler {

    @Value("${alert-api.zone}")
    private String zoneId;

    @Value("${alert-api.actual-filter}")
    private boolean isOnlyActualAlerts;

    @Value("${alert-api.in-path}")
    private String outputPath;

    @Value("${alert-api.out-path}")
    private String inputPath;

    private final AlertRepository alertRepository = AlertRepository.getInstance();

    @PostConstruct
    public void getAlertsFromBackupFile() {
        List<ServiceAlert> tempList = null;
        long dateTimeNow = LocalDateTime.now()
                .atZone(ZoneId.of(zoneId))
                .toEpochSecond();
        var serviceAlertList = alertRepository.getServiceAlertsList();
        try {
            var fromFile = new FileInputStream(inputPath);
            var objectFromFile = new ObjectInputStream(fromFile);

            if (fromFile != null) {
                tempList = (List<ServiceAlert>) objectFromFile.readObject();
                log.info("Got {} alerts from file and added to temp list", tempList.size());
                if (isOnlyActualAlerts) {
                    serviceAlertList.addAll(tempList.stream()
                            .filter(alert -> alert.getActiveWindows()
                                    .stream()
                                    .anyMatch(element -> element.getLongTo(zoneId) > dateTimeNow))
                            .toList());
                } else {
                    serviceAlertList.addAll(tempList);
                }
                log.info("{} alerts from file filtered and added to list", serviceAlertList.size());
            }
            objectFromFile.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @PreDestroy
    @Scheduled(cron = "@daily")
    public void saveAlertsToFile() {
        var serviceAlertList = alertRepository.getServiceAlertsList();
        try {
            var toFile = new FileOutputStream(outputPath);
            var objectToFile = new ObjectOutputStream(toFile);
            objectToFile.writeObject(serviceAlertList);

            log.info("Successfully safe {} alerts to back-up file : {}", serviceAlertList.size(), outputPath);

            objectToFile.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
