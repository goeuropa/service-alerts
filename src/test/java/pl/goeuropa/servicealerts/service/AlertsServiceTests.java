package pl.goeuropa.servicealerts.service;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.goeuropa.servicealerts.cache.CacheManager;
import pl.goeuropa.servicealerts.model.serviceAlerts.ServiceAlert;

import java.util.Arrays;
import java.util.List;

import static java.nio.charset.Charset.forName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class AlertsServiceTests {

    @Autowired
    private AlertService testService;

    private final CacheManager inst = CacheManager.getInstance();

    private EasyRandom getGenerator() {
       EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(2)
                .objectPoolSize(2)
                .randomizationDepth(2)
                .charset(forName("UTF-8"))
                .stringLengthRange(5, 6)
                .collectionSizeRange(2, 2)
                .ignoreRandomizationErrors(true);
        EasyRandom generator = new EasyRandom(parameters);
        return generator;
    }

    private List<ServiceAlert> alertListInit() {
        var alert1 = getGenerator().nextObject(ServiceAlert.class);
        alert1.setAgencyId("-77");
        var alert2 = getGenerator().nextObject(ServiceAlert.class);
        alert2.setAgencyId("-11");
        var alert3 = getGenerator().nextObject(ServiceAlert.class);
        alert3.setAgencyId("-77");
        return Arrays.asList(alert1, alert2, alert3);
    }

    @Test
    void createDeleteAlertTest() {
        int beforeCreateCount = inst.getServiceAlertsList().size();
        ServiceAlert test = getGenerator().nextObject(ServiceAlert.class);

        testService.createAlert(test);
        assertEquals(beforeCreateCount + 1, testService.getAlertList().size());

        testService.deleteAlertById(test.getId());
        assertEquals(beforeCreateCount, inst.getServiceAlertsList().size());
    }

    @Test @Disabled
    void getAlertsByAgencyId() {
        //when
        inst.getServiceAlertsList().addAll(alertListInit());
        List<ServiceAlert> filteredAlerts = testService.getAlertListByAgency("-77");
        //then
        assertFalse(filteredAlerts.isEmpty());
        assertEquals(2, filteredAlerts.size());
        testService.clearAlertList();
    }
}
