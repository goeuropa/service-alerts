package pl.goeuropa.servicealerts.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.goeuropa.servicealerts.model.ServiceAlert;

import java.util.*;

@Slf4j
public class AlertRepository {

    /**
     * Make this class available as a singleton
     */
    private static final AlertRepository singleton = new AlertRepository();

    @Getter
    private final List<ServiceAlert> serviceAlertList = Collections.synchronizedList(new LinkedList<>());

    /**
     * Gets the singleton instance of this class.
     *
     * @return
     */
    public static AlertRepository getInstance() {
        return singleton;
    }

    /**
     * Constructor declared private to enforce only access to this singleton
     * class being getInstance()
     */
    private AlertRepository() {
    }

    /**
     * Set of agency IDs.
     */
    @Getter
    private final Set<String> agencyIds = new HashSet<>();

    public void addToAlertList(ServiceAlert alert) {
        agencyIds.add(alert.getAgencyId());
        serviceAlertList.add(alert);
        log.info("Add to list a service alert : {}", alert);
    }

    public void clearServiceAlertsList() {
        serviceAlertList.clear();
        log.info("List of alerts is cleared: {}", serviceAlertList.isEmpty());
    }
}
