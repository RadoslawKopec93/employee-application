package employee.application.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {
    
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String authorizeUserAndCreateToken(Map<String, String> payload) {
        String code = payload.get("code");

        if (code == null || code.isEmpty()) {
            return "BRAK";
        }

        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> body = Map.of(
            "client_id", clientId,
            "client_secret", clientSecret,
            "code", code
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("TEST");
                return "JEST";
            } else {
                System.out.println("TEST2");

                return "BRAK2";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "errer";
        }
    }
}
