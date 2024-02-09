package pl.goeuropa.goeuropaservicealerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.transit.realtime.GtfsRealtime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.ServiceAlert;
import pl.goeuropa.goeuropaservicealerts.service.AlertService;
import pl.goeuropa.goeuropaservicealerts.service.AlertServiceImpl;


@Slf4j
@RestController
@RequestMapping("/api")
public class AlertController {

    private final AlertService service = new AlertServiceImpl();

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ServiceAlert alert) throws JsonProcessingException {
        log.info("Alert has been post : {}", alert);
        service.createAlert(alert);
    }


    @RequestMapping(value = "/{agencyId}/alerts.pb", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    public @ResponseBody GtfsRealtime.FeedMessage getAsFile(@PathVariable("agencyId") String agencyId) {
        GtfsRealtime.FeedMessage feed = service.getAlertsByAgency(agencyId);
        log.info("_______________________________________________");
        return feed;
    }

    @GetMapping(value = "/alerts", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    public ResponseEntity<StreamingResponseBody> getAllAsFile() {
        GtfsRealtime.FeedMessage feed = service.getAlerts();
        log.info("_______________________________________________");
        StreamingResponseBody stream = feed::writeTo;
        return ResponseEntity.ok(stream);
    }
}
