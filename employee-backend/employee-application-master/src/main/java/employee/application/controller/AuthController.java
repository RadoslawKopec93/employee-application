package employee.application.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import employee.application.model.interfaces.ApiResponse;
import employee.application.services.AuthService;

@RestController
@RequestMapping(path = "/")
public class AuthController {

    private final RestTemplate restTemplate = new RestTemplate();

    private SecureService secureService;

    private AuthService authService;

    public AuthController(AuthService authService, SecureService secureService) {
        this.authService = authService;
        this.secureService = secureService;
    }

    private final String clientId = "Ov23li74ydf1wrP9LsLX";
    private final String clientSecret = "c761e1dea110ff826bc630d2eeb4873a05574e69";

    @PostMapping("/api/github-auth")
    public ResponseEntity<ApiResponse> handleGithubAuth(@RequestBody Map<String, String> payload) {
        System.out.println("TEST1");
        return authService.authorizeUserAndCreateToken(payload);
    }

    @PostMapping("/refresh-token")
    public String refreshToken(@RequestBody String entity) {
        //TODO: process POST request

        return entity;
    }

   // @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/test")
    public String testRefresh() {
        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication());
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH: " + auth);
        System.out.println("AUTHORITIES: " + auth.getAuthorities());
        System.out.println("SECURE SERVICE CLASS: " + secureService.getClass());

        return secureService.testLogic();

        //return "UDAŁO SIE";
    }
}
