package pl.goeuropa.servicealerts.controller;


import com.google.transit.realtime.GtfsRealtime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.goeuropa.servicealerts.model.serviceAlerts.ServiceAlert;
import pl.goeuropa.servicealerts.service.AlertService;
import pl.goeuropa.servicealerts.service.AlertServiceImpl;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api")
public class AlertController {


    private final AlertService service = new AlertServiceImpl();

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ServiceAlert alert) {
        log.info("Alert has been post : {}", alert);
        service.createAlert(alert);
    }

    @RequestMapping(value = "/{agencyId}/alerts.pb", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<StreamingResponseBody> getByAgencyAsFile(@PathVariable("agencyId") String agencyId) {
        try {
            GtfsRealtime.FeedMessage feed = service.getAlertsByAgency(agencyId);
            log.info("Got {} service-alerts as protobuf file", feed.getSerializedSize());
            StreamingResponseBody stream = feed::writeTo;
            return ResponseEntity.ok().body(stream);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/alerts.pb", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    public ResponseEntity<StreamingResponseBody> getAllAsFile() {
        try {
            GtfsRealtime.FeedMessage feed = service.getAlerts();
            log.info("Got {} service-alerts as protobuf file", feed.getSerializedSize());
            StreamingResponseBody stream = feed::writeTo;
            return ResponseEntity.ok().body(stream);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<ServiceAlert>> getAllAsJson() {
        try {
            List<ServiceAlert> alertList = service.getAlertList();
            log.info("Got {} service-alerts as protobuf file", alertList.size());
            return ResponseEntity.ok().body(alertList);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{agencyId}/alerts")
    public ResponseEntity<List<ServiceAlert>> getByAgencyIdAsJson(@PathVariable("agencyId") String agencyId) {
        try {
            List<ServiceAlert> alertList = service.getAlertListByAgency(agencyId);
            log.info("Got {} service-alerts as protobuf file", alertList.size());
            return ResponseEntity.ok().body(alertList);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }
}
