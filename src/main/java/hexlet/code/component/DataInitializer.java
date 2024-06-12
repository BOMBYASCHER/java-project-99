package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var admin = new UserCreateDTO();
        admin.setEmail("hexlet@example.com");
        admin.setPassword("qwerty");
        userService.create(admin);
    }
}
