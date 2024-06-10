package hexlet.code.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
    private User anotherUser;
    private UserCreateDTO testUserCreate;
    private UserCreateDTO anotherUserCreate;

    @BeforeEach
    void setUp() {
        testUser = Instancio.of(modelGenerator.getUser()).create();
        testUserCreate = new UserCreateDTO();
        testUserCreate.setEmail(testUser.getEmail());
        testUserCreate.setPassword(testUser.getPassword());

        anotherUser = Instancio.of(modelGenerator.getUser()).create();
        anotherUserCreate = new UserCreateDTO();
        anotherUserCreate.setEmail(anotherUser.getEmail());
        anotherUserCreate.setPassword(anotherUser.getPassword());

    }

    private String createAndAuthenticate(User user) throws Exception {
        var email = user.getEmail();
        var password = user.getPassword();
        var userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail(email);
        userCreateDTO.setPassword(password);
        userService.create(userCreateDTO);
        var id = userRepository.findByEmail(email).get().getId();
        user.setId(id);
        var data = Map.of(
                "username", user.getUsername(),
                "password", password
        );
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var token = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return "Bearer " + token;
    }

    @Test
    void testIndex() throws Exception {
        var request = get("/api/users")
                .with(jwt());
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
        var request = get("/api/users/" + testUser.getId())
                .with(jwt());
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
        var request = post("/api/users")
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
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUser))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        var token = createAndAuthenticate(testUser);
        var testUserId = testUser.getId();
        var passwordBeforeUpdate = userRepository.findById(testUserId).get().getPassword();
        var update = Map.of("password", "newP4ssW0rd");
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isNotEqualTo(passwordBeforeUpdate);
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        var token = createAndAuthenticate(testUser);
        var testUserId = testUser.getId();
        var passwordBeforeUpdate = userRepository.findById(testUserId).get().getPassword();
        var update = Map.of("password", "12");
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isEqualTo(passwordBeforeUpdate);
    }

    @Test
    void testDelete() throws Exception {
        var token = createAndAuthenticate(testUser);
        var request = delete("/api/users/" + testUser.getId())
                .header("Authorization", token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findById(testUser.getId());
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    void testUpdatingForeignUserData() throws Exception {
        userRepository.save(testUser);
        var anotherUserResponseToken = createAndAuthenticate(anotherUser);
        var savedTestUser = userRepository.findByEmail(testUser.getEmail()).get();
        var passwordBeforeAttack = savedTestUser.getPassword();
        var testUserId = savedTestUser.getId();
        var update = Map.of("password", "newP4ssW0rd");
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .header("Authorization", anotherUserResponseToken);
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
        var testUserAfterAttack = userRepository.findById(testUserId).get();
        assertThat(testUserAfterAttack).isNotNull();
        assertThat(testUserAfterAttack.getPassword()).isEqualTo(passwordBeforeAttack);
    }
}
