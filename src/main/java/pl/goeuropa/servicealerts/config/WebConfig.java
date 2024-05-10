package pl.goeuropa.servicealerts.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
                .allowedOrigins("http://localhost:3000",
                        "https://alerts.goeuropa.net",
                        "https://transitclock.goeuropa.net",
                        "https://swarzedz.goeuropa.net",
                        "http://goeuropa.net/")
                .allowedOriginPatterns("http://localhost:3000",
                        "https://alerts.goeuropa.net",
                        "https://transitclock.goeuropa.net",
                        "https://swarzedz.goeuropa.net",
                        "http://goeuropa.net")
        ;
    }
}
