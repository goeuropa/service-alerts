package pl.goeuropa.servicealerts.controller;


import com.google.transit.realtime.GtfsRealtime;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pl.goeuropa.servicealerts.service.AlertService;


@Slf4j
@RestController
@RequestMapping("/pb")
public class ProtoAlertController {

    private final AlertService service;

    public ProtoAlertController(AlertService service) {
        this.service = service;
    }

    @GetMapping(value = "/agency/{agencyId}/alerts", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    @Operation(summary = "Return a protobuf with alerts for request agency Id")
    public ResponseEntity<Object> getByAgencyAsFile(@PathVariable String agencyId) {
        try {
            GtfsRealtime.FeedMessage feedMessage = service.getAlertsByAgency(agencyId);
            log.info("Got {} service-alerts as protobuf file", feedMessage.getSerializedSize());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PROTOBUF);
            return ResponseEntity.ok()
                    .headers(headers)
                    .cacheControl(CacheControl.noCache())
                    .body(feedMessage);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value = "/alerts", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    @Operation(summary = "Return a protobuf sorted by creation time with all alerts from alerts object list")
    public ResponseEntity<Object> getAllAsFile() {
        try {
            GtfsRealtime.FeedMessage feedMessage = service.getAlerts();
            log.info("Got {} service-alerts as protobuf file", feedMessage.getSerializedSize());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PROTOBUF);
            return ResponseEntity.ok()
                    .headers(headers)
                    .cacheControl(CacheControl.noCache())
                    .body(feedMessage);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.noContent().build();
        }
    }
}
