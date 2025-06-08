package employee.application.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import employee.application.model.RequestMessage;
import employee.application.model.Role;
import employee.application.model.User;
import employee.application.model.UserLoginDTO;
import employee.application.model.UserOauth2ServerInformation;
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
                UserOauth2ServerInformation userOauth2ServerInformation = getUserInformation(response);

                User user = createOauthUserIfnew(
                        id -> userRepository.findByProviderId(id),
                        userOauth2ServerInformation.id(), "GITHUB", userOauth2ServerInformation.email(), null);

                if (user != null) {
                    ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "token")
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(7 * 24 * 60 * 60)
                            .sameSite("None")
                            .build();

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
                    String token = jwtService.generateToken(user.getEmail(), user.getRolesTypes());

                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .headers(httpHeaders)
                            .body(new UserToken(token));
                }

                ResponseEntity<ApiResponse> a = ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new RequestMessage("Cannot find or create user"));

                return a;
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestMessage("Github validation failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RequestMessage("Validation failed"));
        }
    }

    private void requestGitForUserData(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
    }

    public UserOauth2ServerInformation getUserInformation(ResponseEntity<Map> response) {
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

                System.out.println("USER INFO" + userInfo);

                ResponseEntity<List<Map<String, Object>>> emailResponse = restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        userRequest,
                        new ParameterizedTypeReference<>() {
                }
                );

                String email = emailResponse.getBody().stream()
                        .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                        .map(e -> (String) e.get("email"))
                        .findFirst()
                        .orElse(null);

                if (email == null) {
                    throw new NullPointerException("Email is null");
                }

                Object idObj = userInfo.get("id");
                if (idObj instanceof Number number) {
                    long id = number.longValue();
                    return new UserOauth2ServerInformation(String.valueOf(id), email);
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

    public User createOauthUserIfnew(Function<String, Optional<User>> lookupFunction,
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
                user.setRoles(Set.of(role));
                return userRepository.save(user);
            }
            return userExist.get();
        } catch (Exception e) {
            System.err.println("Error while creating a new user:" + e.getMessage());
            return null;
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
