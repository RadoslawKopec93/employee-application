package employee.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /*   @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests( auth -> {
            auth.requestMatchers("/t").permitAll();
            auth.anyRequest().authenticated();
        })
        .oauth2Login(withDefaults())
        .formLogin(withDefaults())
        .build();
    } */
    @Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(auth -> {
        auth.requestMatchers("/t", "/t/").permitAll(); // <--- dostęp bez logowania
        auth.anyRequest().authenticated();     // wszystko inne wymaga autoryzacji
    })
    .oauth2Login(withDefaults())              // logowanie przez OAuth2 (np. Keycloak)
    .formLogin(withDefaults())                // logowanie formularzowe (fallback)
    .build();
}

}
