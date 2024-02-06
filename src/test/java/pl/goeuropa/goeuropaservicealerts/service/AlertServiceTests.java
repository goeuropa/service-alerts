package pl.goeuropa.goeuropaservicealerts.service;

import com.google.transit.realtime.GtfsRealtime.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AlertServiceTests {

    @Autowired
    AlertService testService = new AlertServiceImpl();

    @Test
    void createAlert() {
        List<EntitySelector> list = testService
                .createAlert(Alert.Cause.ACCIDENT, Alert.Effect.OTHER_EFFECT)
                .getInformedEntityList();
        list.stream().forEach(System.out::println);
    }
}
