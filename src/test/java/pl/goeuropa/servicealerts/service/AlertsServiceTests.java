package pl.goeuropa.servicealerts.service;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.goeuropa.servicealerts.repository.AlertRepository;
import pl.goeuropa.servicealerts.model.ServiceAlert;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.Charset.forName;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class AlertsServiceTests {

    @Autowired
    private AlertService testService;

    private final AlertRepository inst = AlertRepository.getInstance();

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
        alert1.setId("-1");
        var alert2 = getGenerator().nextObject(ServiceAlert.class);
        alert2.setAgencyId("-17");
        alert2.setId("-11");
        var alert3 = getGenerator().nextObject(ServiceAlert.class);
        alert3.setAgencyId("-77");
        alert3.setId("-111");
        return Arrays.asList(alert1, alert2, alert3);
    }

    @Test
    void createDeleteAlertTest() {
        int beforeCreateCount = inst.getServiceAlertList().size();
        ServiceAlert test = getGenerator().nextObject(ServiceAlert.class);

        testService.createAlert(test);
        assertEquals(beforeCreateCount + 1, testService.getAlertList().size());
        // Rollback
        testService.deleteAlertById(test.getId());
        assertEquals(beforeCreateCount, inst.getServiceAlertList().size());
    }

    @Test
    void getAlertsByAgencyId() {

        inst.getServiceAlertList().addAll(alertListInit());
        Set<String> agencyIds = alertListInit().stream()
                .map(ServiceAlert::getAgencyId)
                .collect(Collectors.toSet());

        List<ServiceAlert> filteredAlerts = testService.getAlertListByAgency("-77");
        assertFalse(filteredAlerts.isEmpty());
        assertEquals(2, filteredAlerts.size());
        // Rollback
        agencyIds.forEach(id -> testService.deleteAlertsByAgency(id));
    }

    @Test
    void shouldThrowIfEmptyTest() {
        try {
            testService.getAlertList();
        } catch (RuntimeException ex) {
            assertThrows(IllegalStateException.class, () -> testService.getAlertList());
        }
    }

    @Test
    void updateAlertTest() {
        inst.getServiceAlertList().addAll(alertListInit());
        Set<String> listIds = alertListInit().stream()
                .map(ServiceAlert::getId)
                .collect(Collectors.toSet());

        for (String id : listIds) {
            var testToUpdate = new ServiceAlert();
            testToUpdate.setId(id);
            testToUpdate.setAgencyId(id);
            testService.editAlert(id, testToUpdate);
        }
        assertNull(testService.getAlertList().getFirst().getCause());
        assertNull(testService.getAlertList().getFirst().getEffect());
        assertTrue(testService.getAlertList().stream()
                .map(ServiceAlert::getId)
                .toList().containsAll(listIds));

        // Rollback
        listIds.forEach(id -> testService.deleteAlertById(id));
    }
}
