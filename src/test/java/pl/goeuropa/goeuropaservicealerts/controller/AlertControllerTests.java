package pl.goeuropa.goeuropaservicealerts.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AlertControllerTests {

    @Autowired
    private MockMvc mockMvc;


    private String baseUrl = "/api";

    @Test
    void putNewAlertTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseUrl + "/create")
                        .content("{\"id\":\"JwJjS\",\"creationTime\":174660012457795,\"activeWindows\":[{\"from\":178771028339098860,\"to\":1702528838389824},"+
                                "{\"from\":-4737903668696581858,\"to\":-930024639575167394}],\"cause\":\"ezDhr\",\"effect\":\"Hqsgq\",\"summaries\":"+
                                "[{\"value\":\"lTeRG\",\"lang\":\"pl\"},{\"value\":\"ELxkg\",\"lang\":\"en\"}],\"urls\":[{\"value\":\"http\",\"lang\":\"en\"},"+
                                "{\"value\":\"http\",\"lang\":\"en\"}]"+
                                ",\"allAffects\":[{\"agencyId\":\"wZvKw\",\"routeId\":\"HFLDl\",\"tripId\":\"tDRqD\",\"stopId\":\"ejdlV\",\"routType\":\"sgeJN\"},"+
                                "{\"agencyId\":\"tFXDE\",\"routeId\":\"bXVCS\",\"tripId\":\"FWDEr\",\"stopId\":\"Mcnpz\",\"routType\":\"tSOhh\"}],\"descriptions\":["+
                                "{\"value\":\"ELxkg\",\"lang\":\"en\"},{\"value\":\"lTeRG\",\"lang\":\"en\"}]}")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());


    }
}

