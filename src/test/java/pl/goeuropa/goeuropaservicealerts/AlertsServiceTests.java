package pl.goeuropa.goeuropaservicealerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.ServiceAlert;
import pl.goeuropa.goeuropaservicealerts.service.AlertService;

import static java.nio.charset.Charset.forName;

@SpringBootTest
class AlertsServiceTests {

    @Autowired
    private AlertService testService;

    @Test
    void createNewAlertTest() throws JsonProcessingException {
        EasyRandomParameters parameters = new EasyRandomParameters()
            .seed(2)
            .objectPoolSize(2)
            .randomizationDepth(2)
            .charset(forName("UTF-8"))
            .stringLengthRange(5, 6)
            .collectionSizeRange(2, 2)
            .ignoreRandomizationErrors(true);
        EasyRandom generator = new EasyRandom(parameters);
        testService.createAlert(generator.nextObject(ServiceAlert.class));
        //TODO get ending test
    }

}
