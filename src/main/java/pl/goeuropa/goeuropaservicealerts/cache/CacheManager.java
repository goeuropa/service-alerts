package pl.goeuropa.goeuropaservicealerts.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.ServiceAlert;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@EnableScheduling
@Component
public class CacheManager implements Serializable {

    /**
     * Make this class available as a singleton
     */
    private static CacheManager singleton = new CacheManager();


    @Value("${alert-api.zoneId}")
    private String ZONE_ID;

    @Value("${alert-api.inPath}")
    private String INPUT_PATH;

    @Value("${alert-api.outPath}")
    private String OUTPUT_PATH;

    private static List<ServiceAlert> serviceAlertList = new LinkedList<>();

    /**
     * Gets the singleton instance of this class.
     *
     * @return
     */
    public static CacheManager getInstance() {
        return singleton;
    }

    /**
     * Constructor declared private to enforce only access to this singleton
     * class being getInstance()
     */
    private CacheManager() {
    }


    /**
     * Add the alert to list.
     */
    public void addToAlertList(ServiceAlert alert) {
        log.info("Add to cache a service alert : {}", alert);
        serviceAlertList.add(alert);
    }


    /**
     * Add all alerts from cache file to list.
     */


    @PostConstruct
    public void getAllAlertsFromFile() {
        List<ServiceAlert> tempList = null;
        long dateTimeNow = LocalDateTime.now()
                .atZone(ZoneId.of(ZONE_ID))
                .toEpochSecond();
        try {
            var fromFile = new FileInputStream(INPUT_PATH);
            var objectFromFile = new ObjectInputStream(fromFile);

            if (objectFromFile != null) {
                tempList = (List<ServiceAlert>) objectFromFile.readObject();
                log.info("Got {} alerts from file and added to temp list", tempList.size());
                tempList.stream()
                        .flatMap(alert -> alert.getActiveWindows()
                                .stream()
                                .filter(element -> element.getTo() > dateTimeNow));
                serviceAlertList = tempList;
                log.info("{} alerts from file filtered and added to list", serviceAlertList.size());
            }

            objectFromFile.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Save all alerts from cache to file.
     */
    @PreDestroy
    @Scheduled(cron = "@hourly")
    public void saveAllAlertsToFile() {
        try {
            var toFile = new FileOutputStream(OUTPUT_PATH);
            var objectToFile = new ObjectOutputStream(toFile);
            objectToFile.writeObject(serviceAlertList);

            log.info("Succesfully safe {} alerts to cache list file : {}", serviceAlertList.size(), OUTPUT_PATH);

            objectToFile.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<ServiceAlert> getServiceAlertsList () {
        return serviceAlertList;
    }
}
