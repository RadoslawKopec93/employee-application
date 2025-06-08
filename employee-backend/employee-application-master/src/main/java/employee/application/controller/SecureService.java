package employee.application.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class SecureService {

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public String testLogic() {
        System.out.println("🔥 testLogic weszło");

        return "Zadziałało!";
    }
}
