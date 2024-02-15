package pl.goeuropa.servicealerts.controller;


import com.google.transit.realtime.GtfsRealtime;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.goeuropa.servicealerts.model.servicealerts.ServiceAlert;
import pl.goeuropa.servicealerts.service.AlertService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api")
public class AlertController {

    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create an alert and put to alerts object list")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ServiceAlert alert) {
        log.info("Alert has been post : {}", alert);
        service.createAlert(alert);
    }

    @RequestMapping(value = "/{agencyId}/alerts.pb", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    @Operation(summary = "Return a protobuf file with alerts for request agency Id")
    public ResponseEntity<StreamingResponseBody> getByAgencyAsFile(@PathVariable String agencyId) {
        try {
            GtfsRealtime.FeedMessage feed = service.getAlertsByAgency(agencyId);
            log.info("Got {} service-alerts as protobuf file", feed.getSerializedSize());
            StreamingResponseBody stream = feed::writeTo;
            return ResponseEntity.ok().body(stream);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/alerts.pb", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    @Operation(summary = "Return a protobuf file sorted by creation time with all alerts from alerts object list")
    public ResponseEntity<StreamingResponseBody> getAllAsFile() {
        try {
            GtfsRealtime.FeedMessage feed = service.getAlerts();
            log.info("Got {} service-alerts as protobuf file", feed.getSerializedSize());
            StreamingResponseBody stream = feed::writeTo;
            return ResponseEntity.ok().body(stream);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/alerts")
    @Operation(summary = "Return a JSON sorted by creation time with all alerts from alerts object list")
    public ResponseEntity<List<ServiceAlert>> getAllAsJson() {
        try {
            List<ServiceAlert> alertList = service.getAlertList();
            log.info("Got {} service-alerts", alertList.size());
            return ResponseEntity.ok().body(alertList);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{agencyId}/alerts")
    @Operation(summary = "Return a JSON with alerts for request agency Id")
    public ResponseEntity<List<ServiceAlert>> getByAgencyIdAsJson(@PathVariable String agencyId) {
        try {
            List<ServiceAlert> alertList = service.getAlertListByAgency(agencyId);
            log.info("Got {} service-alerts for agency ID : {}", alertList.size(), agencyId);
            return ResponseEntity.ok().body(alertList);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/alert/delete")
    @Operation(summary = "Delete all alerts from list")
    public String deleteAllServiceAlerts(@RequestParam String alertId) {
        try {
            service.deleteAlertById(alertId);
            log.info("Deleted alert with id : {}", alertId);
            return String.format("Deleted alert with id : %s", alertId);
        } catch (Exception ex) {
            return ex.getMessage() + ex.getCause();
        }
    }

    @PutMapping("/alerts/clean")
    @Operation(summary = "Delete all alerts from list")
    public String deleteAllServiceAlerts() {
        try {
            service.cleanAlertList();
            log.info("Deleted all service alerts from list");
            return "Deleted all service alerts from list";
        } catch (Exception ex) {
            return ex.getMessage() + ex.getCause();
        }
    }
}
