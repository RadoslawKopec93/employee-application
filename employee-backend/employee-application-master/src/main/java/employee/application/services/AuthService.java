package employee.application.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import employee.application.model.RequestMessage;
import employee.application.model.Role;
import employee.application.model.User;
import employee.application.model.UserLoginDTO;
import employee.application.model.UserToken;
import employee.application.model.enums.RoleType;
import employee.application.model.interfaces.ApiResponse;
import employee.application.repository.RoleRepository;
import employee.application.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String PAYLOAD_CODE = "code";

    /*   public ResponseEntity<String> authorizeUserAndCreateToken(User user) {
        JpaRepository userRepo = userRepository;
    } */
    public ResponseEntity<ApiResponse> authorizeUserAndCreateToken(Map<String, String> payload) {
        String code = payload.get(PAYLOAD_CODE);

        if (code == null || code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestMessage("Provided code is not valid"));
        }

        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                PAYLOAD_CODE, code
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatusCode.valueOf(200)) {
                String providerId = getUserInformationId(response);

                Boolean authenticationProcessPassed = createOauthUserIfnew(
                        id -> userRepository.findByProviderId(id),
                        providerId, "GITHUB", null, null);
                // createOauthUserIfnew(providerId);
                if (authenticationProcessPassed) {
                    String token = jwtService.generateToken(providerId, Set.of(RoleType.EMPLOYEE));
                    return ResponseEntity.status(HttpStatus.OK).body(new UserToken(token));
                }

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestMessage("Cannot find or create user"));
            } else {
                System.out.println("TEST2");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestMessage("Github validation failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestMessage("Validation failed"));
        }
    }

    public String getUserInformationId(ResponseEntity<Map> response) {
        if (response.getBody() != null) {
            if (response.getBody().get("access_token") != null) {
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
                if (userInfo == null) {
                    System.err.println("Auth service getUserInformationId userInfo is null");
                    return null;
                }
                if (userInfo.get("id") == null) {
                    System.err.println("Auth service getUserInformationId userInfo.get(key) is null");
                    return null;
                }

                Object idObj = userInfo.get("id");
                if (idObj instanceof Number number) {
                    long id = number.longValue();
                    return String.valueOf(id);
                } else {
                    System.err.println("Auth service getUserInformationId idObj is null");
                    return null;
                }
            }
            System.err.println("Auth service getUserInformationId response.getBody().get(\"access_token\") is null");
            return null;
        }
        System.err.println("Auth service getUserInformationId response.getBody() is null");
        return null;
    }

    public boolean createOauthUserIfnew(Function<String, Optional<User>> lookupFunction,
            String providerId,
            String provider,
            String email,
            String password) {
        try {
            Optional<User> userExist = lookupFunction.apply(providerId);
            if (!userExist.isPresent()) {
                Role role = roleRepository.findByRoleType(RoleType.EMPLOYEE).get();
                User user = new User();
                user.setProviderId(providerId);
                user.setProvider(provider);
                user.setEmail(email);
                user.setPassword(hashPassword(password));
                user.setRoles(Set.of(role));
                userRepository.save(user);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error while creating a new user:" + e.getMessage());
            return false;
        }
    }

    public ResponseEntity<ApiResponse> loginFormUser(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.email() != null && userLoginDTO.password() != null) {
            User user = userRepository
                    .findByEmail(userLoginDTO.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Boolean passwordMatch = authPassword(userLoginDTO.password(), user.getPassword());
            if (passwordMatch) {
                return ResponseEntity.status(HttpStatus.OK).body(new UserToken(jwtService.generateToken(userLoginDTO.email(), user.getRolesTypes())));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestMessage("Cannot authorize user"));
    }

    /* 
    public ResponseEntity<String> loginFormUser(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.email() != null && userLoginDTO.password() != null) {
            Boolean authProcessPassed = createOauthUserIfnew(
                    email -> userRepository.findByEmail(email),
                    null,
                    null,
                    userLoginDTO.email(),
                    userLoginDTO.password()
            );

            if (authProcessPassed) {

            }
        }
    } */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean authPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
