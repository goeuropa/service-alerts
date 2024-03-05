package pl.goeuropa.servicealerts.service;


import com.google.transit.realtime.GtfsRealtime;
import pl.goeuropa.servicealerts.model.servicealerts.ServiceAlert;

import java.util.LinkedList;
import java.util.List;

public interface AlertService {

    void createAlert(ServiceAlert newAlert);

    GtfsRealtime.FeedMessage getAlertsByAgency(String agencyId);

    GtfsRealtime.FeedMessage getAlerts();

    List<ServiceAlert> getAlertListByAgency(String agencyId);

    LinkedList<ServiceAlert> getAlertList();

    void editAlert(String alertId, ServiceAlert newAlert);

    void deleteAlertById(String id);

    void deleteAlertsByAgency(String id);

    void cleanAlertList();
}
