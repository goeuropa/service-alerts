package pl.goeuropa.goeuropaservicealerts.service;

import com.google.transit.realtime.GtfsRealtime;

public interface AlertService {

    GtfsRealtime.Alert.Builder createAlert (GtfsRealtime.Alert.Cause cause, GtfsRealtime.Alert.Effect effect);

}
