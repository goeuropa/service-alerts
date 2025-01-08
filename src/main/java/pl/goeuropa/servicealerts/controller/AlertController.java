package pl.goeuropa.servicealerts.controller;


import com.google.transit.realtime.GtfsRealtime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.goeuropa.servicealerts.model.servicealerts.ServiceAlert;
import pl.goeuropa.servicealerts.service.AlertService;

import java.util.List;


@Slf4j
@Validated
@RestController
@Tag(name = "Service-alerts",
        description = "Service-alert api allow you to provide updates whenever there is disruption on the network. Delays and cancellations of individual trips should usually be communicated using Trip updates.")
@RequestMapping("/api")
public class AlertController {

    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create an alert and put to alerts object list", description = "The 'Id' field has autogenerated values, but it can also be overridden.                                      \n" +
            "\n" +
            "The 'activeWindows' array includes the fields 'from' and 'to' with the following format: 2022-02-02T23:00:15.                                      \n" +
            "\n" +
            "The 'lang' fields with constraint and should have a languages code, for instance: 'en' - english; 'pl' - polish; etc.                                     \n" +
            "\n" +
            "The 'Cause' field has an enum type with rigid values:                                       \n" +
            "- OTHER_CAUSE   \n" +
            "- TECHNICAL_PROBLEM   \n" +
            "- STRIKE   \n" +
            "- DEMONSTRATION   \n" +
            "- ACCIDENT   \n" +
            "- HOLIDAY   \n" +
            "- WEATHER   \n" +
            "- MAINTENANCE   \n" +
            "- CONSTRUCTION   \n" +
            "- POLICE_ACTIVITY   \n" +
            "- MEDICAL_EMERGENCY   \n" +
            "- UNKNOWN_CAUSE   \n" +
            "\n" +
            "The 'Effect' field also has an enum type with rigid values:                                       \n" +
            "- ADDITIONAL_SERVICE   \n" +
            "- DETOUR   \n" +
            "- MODIFIED_SERVICE   \n" +
            "- NO_SERVICE   \n" +
            "- OTHER_EFFECT   \n" +
            "- REDUCED_SERVICE   \n" +
            "- SIGNIFICANT_DELAYS   \n" +
            "- STOP_MOVED   \n" +
            "- UNKNOWN_EFFECT   \n")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ServiceAlert alert) {
        log.info("Alert has been post : {}", alert);
        service.createAlert(alert);
    }

    @GetMapping(value = "/agency/{agencyId}/alerts.pb", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    @Operation(summary = "Return a protobuf file with alerts for request agency Id")
    public ResponseEntity<StreamingResponseBody> getByAgencyAsFile(@PathVariable String agencyId) {
        try {
            GtfsRealtime.FeedMessage feed = service.getAlertsByAgency(agencyId);
            log.info("Get {} service-alerts as protobuf file", feed.getEntityList().size());
            StreamingResponseBody stream = feed::writeTo;
            return ResponseEntity.ok().body(stream);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/alerts.pb", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    @Operation(summary = "Return a protobuf file sorted by creation time with all alerts from alerts object list")
    public ResponseEntity<StreamingResponseBody> getAllAsFile() {
        try {
            GtfsRealtime.FeedMessage feed = service.getAlerts();
            log.info("Get {} service-alerts as protobuf file", feed.getEntityList().size());
            StreamingResponseBody stream = feed::writeTo;
            return ResponseEntity.ok().body(stream);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/alerts")
    @Operation(summary = "Return a JSON sorted by creation time with all alerts from alerts object list")
    public ResponseEntity<List<ServiceAlert>> getAllAsJson() {
        try {
            List<ServiceAlert> alertList = service.getAlertList();
            log.info("Get {} service-alerts", alertList.size());
            return ResponseEntity.ok().body(alertList);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/agency/{agencyId}/alerts")
    @Operation(summary = "Return a JSON with alerts for request agency Id")
    public ResponseEntity<List<ServiceAlert>> getByAgencyIdAsJson(@PathVariable String agencyId) {
        try {
            List<ServiceAlert> alertList = service.getAlertListByAgency(agencyId);
            log.info("Get {} service-alerts for agency ID : {}", alertList.size(), agencyId);
            return ResponseEntity.ok().body(alertList);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/alert/edit")
    @Operation(summary = "Edit alert")
    public String editServiceAlert(@RequestParam String alertId, @RequestBody ServiceAlert alertToUpdate) {
        try {
            service.editAlert(alertId ,alertToUpdate);
            log.info("Edited alert with id : {}", alertToUpdate.getId());
            return String.format("Edited alert with id : %s", alertToUpdate.getId());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    ex.getMessage() + ex.getCause());
        }
    }

    @DeleteMapping("/alert/delete")
    @Operation(summary = "Delete alert from list by alert Id")
    public String deleteServiceAlertById(@RequestParam String alertId) {
        try {
            service.deleteAlertById(alertId);
            log.info("Deleted alert with id : {}", alertId);
            return String.format("Deleted alert with id : %s", alertId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT,
                    ex.getMessage() + ex.getCause());
        }
    }

    @DeleteMapping("/alerts/agency/{agencyId}/clean")
    @Operation(summary = "Delete all alerts for request agency Id")
    public String deleteAllServiceAlertsByAgencyId(@PathVariable String agencyId, @RequestParam String allow) {
        if (allow.equals("yes"))
            try {
                service.deleteAlertsByAgency(agencyId);
                log.info("Deleted all service alerts for agency : {}", agencyId);
                return String.format("Deleted all service alerts for agency : %s", agencyId);
            } catch (Exception ex) {
                return ex.getMessage() + ex.getCause();
            }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                "Please confirm the alert list cleanup by responding with the word \"yes\" in parameters.");
    }

    @DeleteMapping("/alerts/clean")
    @Operation(summary = "Delete all alerts from list")
    public String deleteAllServiceAlerts(@RequestParam String allow) {
        if (allow.equals("yes"))
            try {
                service.cleanAlertList();
                log.info("Deleted all service alerts from list");
                return "Deleted all service alerts from list";
            } catch (Exception ex) {
                return ex.getMessage() + ex.getCause();
            }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                "Please confirm the alert list cleanup by responding with the word \"yes\" in parameters.");
    }
}
