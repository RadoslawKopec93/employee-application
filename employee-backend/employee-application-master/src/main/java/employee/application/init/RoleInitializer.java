package employee.application.init;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import employee.application.model.Role;
import employee.application.model.enums.RoleType;
import employee.application.repository.RoleRepository;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository repository;

    @Override
    public void run(String... args) throws Exception {
        Arrays.stream(RoleType.values()).forEach(type -> {
            repository.findByRoleType(type).orElseGet(() -> {
                Role role = new Role();
                role.setRoleType(type);
                return repository.save(role);
            });
        });
    }
}
