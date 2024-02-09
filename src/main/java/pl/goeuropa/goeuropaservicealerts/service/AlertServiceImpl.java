package pl.goeuropa.goeuropaservicealerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.transit.realtime.GtfsRealtime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import pl.goeuropa.goeuropaservicealerts.cache.CacheManager;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.ServiceAlert;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Slf4j
@EnableScheduling
@Service
public class AlertServiceImpl implements AlertService {

    private final CacheManager cacheManager = CacheManager.getInstance();

    @Value("${alert-api.zoneId}")
    private String ZONE_ID;

    @Override
    public void createAlert(ServiceAlert newAlert) throws JsonProcessingException {
        cacheManager.addToAlertList(newAlert);
        log.info("-- Added new one {}", newAlert);
    }

    @Override
    public GtfsRealtime.FeedMessage getAlertsByAgency(String agencyId) {
        GtfsRealtime.FeedMessage.Builder feed = GtfsRealtime.FeedMessage.newBuilder();
        GtfsRealtime.FeedHeader.Builder header = feed.getHeaderBuilder();
        header.setGtfsRealtimeVersion("2.0");
        long time = LocalDateTime.now()
                .atZone(ZoneId.of(ZONE_ID))
                .toEpochSecond();
        header.setTimestamp(time / 1000);
        //TODO implementation
        return feed.build();
    }

    @Override
    public GtfsRealtime.FeedMessage getAlerts() {
        //TODO implementation
        return null;
    }
}
