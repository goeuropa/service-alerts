package pl.goeuropa.servicealerts.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.goeuropa.servicealerts.model.servicealerts.ServiceAlert;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CacheManager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Make this class available as a singleton
     */
    private static final CacheManager singleton = new CacheManager();


    @Value("${alert-api.zone}")
    private String ZONE_ID;

    @Value("${alert-api.actual-filter}")
    private boolean ONLY_ACTUAL_ALERTS;

    @Value("${alert-api.in-path}")
    private String INPUT_PATH;

    @Value("${alert-api.out-path}")
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

            if (fromFile != null) {
                tempList = (List<ServiceAlert>) objectFromFile.readObject();
                log.info("Got {} alerts from file and added to temp list", tempList.size());
                if (ONLY_ACTUAL_ALERTS) {
                    serviceAlertList = tempList.stream()
                            .filter(alert -> alert.getActiveWindows()
                                    .stream()
                                    .anyMatch(element -> element.getLongTo(ZONE_ID) > dateTimeNow))
                            .collect(Collectors.toList());
                } else serviceAlertList = tempList;
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

            log.info("Successfully safe {} alerts to cache list file : {}", serviceAlertList.size(), OUTPUT_PATH);

            objectToFile.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * Get all alerts from cache list.
     *
     * @return
     */

    public List<ServiceAlert> getServiceAlertsList() {
        return serviceAlertList;
    }


    /**
     * Delete all alerts from cache list.
     */

    public void clearServiceAlertsList() {
        serviceAlertList.clear();
        log.info("List of alerts is cleared: {}", serviceAlertList.isEmpty());
    }
}
