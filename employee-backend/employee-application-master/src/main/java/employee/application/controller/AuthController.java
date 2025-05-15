package employee.application.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import employee.application.services.AuthService;

@RestController
@RequestMapping(path = "/")
public class AuthController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AuthService authService;

    @GetMapping("/tasdas")
    @ResponseBody
    public String home() {
        System.out.println(">> /t was accessed");
        return "Hello";
    }

    /*     @GetMapping("/secured")
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
    } */

 /*     @GetMapping("/api/login-success")
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
    } */
    private final String clientId = "Ov23li74ydf1wrP9LsLX";
    private final String clientSecret = "c761e1dea110ff826bc630d2eeb4873a05574e69";

    @PostMapping("/api/github-auth")
    public String handleGithubAuth(@RequestBody Map<String, String> payload) {
        return authService.authorizeUserAndCreateToken(payload);
    }
}
