package pl.goeuropa.goeuropaservicealerts.service;

import com.google.transit.realtime.GtfsRealtime;

public interface AlertService {

    void createAlert (GtfsRealtime.Alert.Cause cause, GtfsRealtime.Alert.Effect effect);

}
