package pl.goeuropa.goeuropaservicealerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.transit.realtime.GtfsRealtime.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.ServiceAlert;

import java.util.List;

@SpringBootTest
public class AlertServiceTests {

    @Autowired
    AlertService testService;

    @Test
    void createAlert() throws JsonProcessingException {

        testService.createAlert(new ServiceAlert());

    }
}
