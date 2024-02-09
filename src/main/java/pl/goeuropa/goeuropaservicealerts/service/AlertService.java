package pl.goeuropa.goeuropaservicealerts.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.transit.realtime.GtfsRealtime;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.ServiceAlert;

public interface AlertService {

    void createAlert (ServiceAlert newAlert) throws JsonProcessingException;

    GtfsRealtime.FeedMessage getAlertsByAgency (String agencyId);

    GtfsRealtime.FeedMessage getAlerts ();

}
