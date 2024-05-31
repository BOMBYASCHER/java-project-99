package hexlet.code.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = Instancio.of(modelGenerator.getUser()).create();
    }

    @Test
    void testIndex() throws Exception {
        var request = get("/users");
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isArray();
    }

    @Test
    void testShow() throws Exception {
        userRepository.save(testUser);
        var request = get("/users/" + testUser.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).and(
                u -> u.node("email").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    void testCreate() throws Exception {
        var request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser));
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var testUserId = om.readTree(response).get("id").asLong();
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        testUser.setEmail("invalidEmail");
        var request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        userRepository.save(testUser);
        var testUserId = testUser.getId();
        var passwordBeforeUpdate = userRepository.findById(testUserId).get().getPassword();
        var update = Map.of("password", "newP4ssW0rd");
        var request = put("/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isNotEqualTo(passwordBeforeUpdate);
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        userRepository.save(testUser);
        var testUserId = testUser.getId();
        var passwordBeforeUpdate = userRepository.findById(testUserId).get().getPassword();
        var update = Map.of("password", "12");
        var request = put("/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isEqualTo(passwordBeforeUpdate);
    }

    @Test
    void testDelete() throws Exception {
        userRepository.save(testUser);
        var request = delete("/users/" + testUser.getId());
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findById(testUser.getId());
        assertThat(user.isPresent()).isFalse();
    }
}
