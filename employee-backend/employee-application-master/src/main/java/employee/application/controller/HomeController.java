package employee.application.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import employee.application.authorization.JwtService;
import employee.application.services.AuthService;

@RestController
public class HomeController {
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @GetMapping("/t")
    @ResponseBody
    public String home() {
        System.out.println(">> /t was accessed");
        return "Hello";
    }

    @GetMapping("/secured")
    public Map<String, String> secured(@AuthenticationPrincipal Object principal) throws Exception {
        String username;

        if (principal instanceof OAuth2User oAuth2User) {
            username = oAuth2User.getAttribute("login"); // GitHub login
        } else if (principal instanceof User user) {
            username = user.getUsername(); // formLogin
        } else {
            username = principal.toString();
        }

        String jwt = jwtService.generateToken(username);
        return Map.of(
            "message", "Authenticated as " + username,
            "token", jwt
        );
    }

    @GetMapping("/api/login-success")
    public ResponseEntity<Map<String, String>> loginSuccess(@AuthenticationPrincipal OAuth2User principal) throws Exception {
        String username = principal.getAttribute("login"); // np. GitHub login
        System.out.println("USERNAME111 " + username);
        String jwt = jwtService.generateToken(username);

        return ResponseEntity.ok(Map.of(
            "message", "Authenticated as " + username,
            "token", jwt
        ));
    }

    @GetMapping("/token")
    public Map<String, String> token(@AuthenticationPrincipal OAuth2User user) throws Exception {
        String username = user.getAttribute("login"); // dla GitHuba to będzie nazwa użytkownika
        String jwt = jwtService.generateToken(username);
        return Map.of("token", jwt);
    }

    private final String clientId = "Ov23li74ydf1wrP9LsLX";
    private final String clientSecret = "c761e1dea110ff826bc630d2eeb4873a05574e69";

    @PostMapping("/api/github-auth")
    public String handleGithubAuth(@RequestBody Map<String, String> payload) {
        return authService.authorizeUserAndCreateToken(payload);
    }

   /*  @PostMapping("/api/github-auth")
    public ResponseEntity<String> handleGithubAuth(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Brak kodu");
        }

        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> body = Map.of(
            "client_id", "Ov23li74ydf1wrP9LsLX",
            "client_secret", "c761e1dea110ff826bc630d2eeb4873a05574e69",
            "code", code
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("TEST");
                return ResponseEntity.ok("Kod poprawny: " + response.getBody());
            } else {
                System.out.println("TEST2");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kod niepoprawny");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas połączenia z GitHub");
        }
    } */
    
    
/*     @CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
    @PostMapping("/api/github-auth")
    public ResponseEntity<String> handleGitHubCallback(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");

        // Debug
        System.out.println("Otrzymany kod z GitHub: " + code);

        // TODO: sprawdź poprawność kodu (np. wyślij request do GitHub OAuth token endpoint)
        boolean kodJestPoprawny = true; // lub false

        if (kodJestPoprawny) {
            return ResponseEntity.ok("Kod jest prawidłowy");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kod jest nieprawidłowy");
        }
    } */

//@RequestBody Map<String, String> payload
    //@PostMapping("/api/github-auth")
/*     @GetMapping("/api/github-auth")
    public void handleGitHubCallback() {
        System.out.println("TEST DUPA"); */
     /*    System.out.println("TEST DUPA");
        String code = payload.get("code");

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Code is missing");
        }

        RestTemplate restTemplate = new RestTemplate();

        // 1. Wymień code na access token
        String url = "https://github.com/login/oauth/access_token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(url, request, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. Pobierz dane użytkownika z GitHuba
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                userRequest,
                Map.class
        );

        Map<String, Object> user = userResponse.getBody();

        // 3. (opcjonalnie) utwórz JWT, zapisz usera itd.
        // np. String jwt = jwtService.generateToken(user)

        return ResponseEntity.ok(user); */
   // }
}
