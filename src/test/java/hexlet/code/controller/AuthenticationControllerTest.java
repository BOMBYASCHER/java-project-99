package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.AuthenticationDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
    }

    @Test
    void testLogin() throws Exception {
        var email = testUser.getEmail();
        var password = testUser.getPassword();
        var user = new UserCreateDTO();
        user.setEmail(email);
        user.setPassword(password);
        userService.create(user);
        var credentials = new AuthenticationDTO();
        credentials.setUsername(email);
        credentials.setPassword(password);
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(credentials));
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void testLoginNonRegisteredUser() throws Exception {
        var email = testUser.getEmail();
        var password = testUser.getPassword();
        var credentials = new AuthenticationDTO();
        credentials.setUsername(email);
        credentials.setPassword(password);
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(credentials));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
