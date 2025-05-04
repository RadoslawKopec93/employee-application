package employee.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @GetMapping("/t")
    @ResponseBody
    public String home() {
        System.out.println(">> /t was accessed");
        return "Hello";
    }

    @GetMapping("/secured")
    public String secured() {
        return "secured";
    }
}
