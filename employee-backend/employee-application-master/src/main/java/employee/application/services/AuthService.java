package employee.application.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import employee.application.model.User;
import employee.application.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

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
                String providerId = getUserInformationId(response);
                createOauthUserIfnew(providerId);
                System.out.println("RESPONSE");
                System.out.println(response);
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

    public String getUserInformationId(ResponseEntity<Map> response) {
        String accessToken = (String) response.getBody().get("access_token");

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        userHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                userRequest,
                Map.class
        );

        Map userInfo = userResponse.getBody();
        System.out.println("USER INFO:");
        System.out.println(userInfo);
        Object idObj = userInfo.get("id");
        if (idObj instanceof Number) {
            long id = ((Number) idObj).longValue();
            System.out.println("ID: " + id);
            return String.valueOf(id);
        } else {
            return null;
        }
    }

    public void createOauthUserIfnew(String providerId) {
        Boolean userExist = userRepository.findByProviderId(providerId).isPresent();
        if (!userExist) {
            User user = new User();
            user.setProviderId(providerId);
            user.setProvider("GITHUB");
            userRepository.save(user);
        }
    }
}
