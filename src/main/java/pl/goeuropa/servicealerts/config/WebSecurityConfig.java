package pl.goeuropa.servicealerts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${alert-api.security.user}")
    private String user;
    @Value("${alert-api.security.password}")
    private String password;

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails authz = User.withDefaultPasswordEncoder()
                .username(user)
                .password(password)
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(authz);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/**"
                        )
                        .hasRole("ADMIN")
                        // Защищаем остальные запросы
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults());

        return http.build();
    }
}
