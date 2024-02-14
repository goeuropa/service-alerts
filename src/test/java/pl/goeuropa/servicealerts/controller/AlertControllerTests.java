package pl.goeuropa.servicealerts.controller;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.goeuropa.servicealerts.model.serviceAlerts.ServiceAlert;
import pl.goeuropa.servicealerts.service.AlertService;

import java.util.Collections;
import java.util.LinkedList;

import static java.nio.charset.Charset.forName;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AlertControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService mockService;


    private String baseUrl = "/api";

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

    private LinkedList<ServiceAlert> alertListInit() {
        var alert1 = getGenerator().nextObject(ServiceAlert.class);
        alert1.setAgencyId("-77");
        var alert2 = getGenerator().nextObject(ServiceAlert.class);
        alert2.setAgencyId("-11");
        var alert3 = getGenerator().nextObject(ServiceAlert.class);
        alert3.setAgencyId("-77");
        LinkedList<ServiceAlert> testAlerts = new LinkedList<>();
        Collections.addAll(testAlerts, alert1, alert2, alert3);
        return testAlerts;
    }

    @Test
    void postNewAlertTest() throws Exception {
        mockMvc.perform(
                post(baseUrl + "/create")
                        .content("{\"id\":\"-11\",\"agencyId\":\"44\",\"creationTime\":\"null\",\"activeWindows\":[{\"from\":\"2011-12-03T10:15:30\",\"to\":\"2011-12-03T10:15:30\"},"+
                                "{\"from\":\"2011-12-03T10:15:30\",\"to\":\"2011-12-03T10:15:30\"}],\"cause\":\"OTHER_CAUSE\",\"effect\":\"STOP_MOVED\",\"summaries\":"+
                                "[{\"value\":\"someText\",\"lang\":\"pl\"},{\"value\":\"ELxkg\",\"lang\":\"en\"}],\"urls\":[{\"value\":\"http\",\"lang\":\"en\"},"+
                                "{\"value\":\"http\",\"lang\":\"en\"}],\"allAffects\":[{\"routeId\":\"HFLDl\",\"tripId\":\"tDRqD\",\"stopId\":\"ejdlV\","+
                                "\"routType\":\"sgeJN\"},{\"routeId\":\"bXVCS\",\"tripId\":\"FWDEr\",\"stopId\":\"Mcnpz\",\"routType\":\"tSOhh\"}],"+
                                "\"descriptions\":[{\"value\":\"ELxkg\",\"lang\":\"en\"},{\"value\":\"lTeRG\",\"lang\":\"en\"}]}")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void getAlertsAsProtobufTest() throws Exception {
//        LinkedList<ServiceAlert> testList = alertListInit();
        when(mockService.getAlertList()).thenReturn(alertListInit());

        mockMvc.perform(
                        get(baseUrl + "/alerts.pb")
                                .contentType(MediaType.APPLICATION_PROTOBUF_VALUE)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
        }

    @Test
    void getAlertsAsJsonTest() throws Exception {
//        LinkedList<ServiceAlert> testList = alertListInit();
        when(mockService.getAlertList()).thenReturn(alertListInit());

        mockMvc.perform(
                get(baseUrl + "/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$[0]").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].agencyId").value("-77"))
                .andExpect(jsonPath("$[0].creationTime").isNumber())
                .andExpect(jsonPath("$[0].balance").doesNotExist())
                .andExpect(jsonPath("$[0].activeWindows").isArray())
                .andExpect(jsonPath("$[0].descriptions").isArray())
        ;
    }
}
