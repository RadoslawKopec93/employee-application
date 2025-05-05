package employee.application.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import employee.application.authorization.JwtService;

@RestController
public class HomeController {
    
    @Autowired
    private JwtService jwtService;

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
}
