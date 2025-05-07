package employee.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import employee.application.authorization.JwtAuthenticationFilter;

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
/*     @Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(auth -> {
        auth.requestMatchers("/t", "/t/").permitAll(); // <--- dostęp bez logowania
        auth.anyRequest().authenticated();     // wszystko inne wymaga autoryzacji
    })
    .oauth2Login(withDefaults())              // logowanie przez OAuth2 (np. Keycloak)
    .formLogin(withDefaults())                // logowanie formularzowe (fallback)
    .build();
} */
   private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/t", "/token", "/login**","/api/github-auth").permitAll()
                .anyRequest().authenticated()
            )/* 
            .oauth2Login(oauth -> oauth
                .defaultSuccessUrl("/api/login-success", true) // <--- tu przekierowanie po sukcesie
            ) */
             .formLogin(form -> form
                .defaultSuccessUrl("/secured", true) // <--- i tu
            )
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
