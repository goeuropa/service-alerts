package pl.goeuropa.servicealerts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ServiceAlertsApi {

    private static final Logger log = LoggerFactory.getLogger(ServiceAlertsApi.class);

    public static void main(String[] args) {
        SpringApplication.run(ServiceAlertsApi.class, args);

        log.info(" ServiceAlerts OpenApi: [ http://localhost:8888/ui ]");
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }
}
