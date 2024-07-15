package hexlet.code.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.AuthenticationUtil;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private AuthenticationUtil authenticationUtil;

    private User testUser;
    private User anotherUser;
    private UserCreateDTO testUserCreate;
    private UserUpdateDTO testUserUpdateDTO;

    @BeforeEach
    void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        testUserCreate = Instancio.of(modelGenerator.getUserCreateDTOModel()).create();
        testUserUpdateDTO = Instancio.of(modelGenerator.getUserUpdateDTOModel()).create();
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
                .content(om.writeValueAsString(testUserCreate));
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var userId = om.readTree(response).get("id").asLong();
        var user = userRepository.findById(userId).get();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(testUserCreate.getEmail());
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        testUserCreate.setEmail("invalidEmail");
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUserCreate))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        userRepository.save(testUser);
        var token = authenticationUtil.generateBearerToken(testUser);
        var testUserId = testUser.getId();
        var passwordBeforeUpdate = userRepository.findById(testUserId).get().getPassword();
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUserUpdateDTO))
                .header(authenticationUtil.header(), token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isNotEqualTo(passwordBeforeUpdate);
    }

    @Test
    void testPartialUpdate() throws Exception {
        userRepository.save(testUser);
        var token = authenticationUtil.generateBearerToken(testUser);
        var testUserId = testUser.getId();
        var update = new UserUpdateDTO();
        update.setFirstName(testUserUpdateDTO.getFirstName());
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .header(authenticationUtil.header(), token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(update.getFirstName().get());
    }

    @Test
    void testPartialUpdateWithInvalidData() throws Exception {
        userRepository.save(testUser);
        var token = authenticationUtil.generateBearerToken(testUser);
        var testUserId = testUser.getId();
        var update = new UserUpdateDTO();
        update.setPassword(null);
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .header(authenticationUtil.header(), token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isEqualTo(testUser.getPassword());
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        userRepository.save(testUser);
        var token = authenticationUtil.generateBearerToken(testUser);
        var testUserId = testUser.getId();
        var passwordBeforeUpdate = userRepository.findById(testUserId).get().getPassword();
        testUserUpdateDTO.setPassword(JsonNullable.of("12"));
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUserUpdateDTO))
                .header(authenticationUtil.header(), token);
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        var user = userRepository.findById(testUserId).get();
        assertThat(user).isNotNull();
        assertThat(user.getPassword()).isEqualTo(passwordBeforeUpdate);
    }

    @Test
    void testDelete() throws Exception {
        userRepository.save(testUser);
        var token = authenticationUtil.generateBearerToken(testUser);
        var request = delete("/api/users/" + testUser.getId())
                .header(authenticationUtil.header(), token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        var user = userRepository.findById(testUser.getId());
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    void testUpdatingForeignUserData() throws Exception {
        userRepository.save(testUser);
        userRepository.save(anotherUser);
        var anotherUserResponseToken = authenticationUtil.generateBearerToken(anotherUser);
        var savedTestUser = userRepository.findByEmail(testUser.getEmail()).get();
        var passwordBeforeAttack = savedTestUser.getPassword();
        var testUserId = savedTestUser.getId();
        var request = put("/api/users/" + testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testUserUpdateDTO))
                .header(authenticationUtil.header(), anotherUserResponseToken);
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
        var testUserAfterAttack = userRepository.findById(testUserId).get();
        assertThat(testUserAfterAttack).isNotNull();
        assertThat(testUserAfterAttack.getPassword()).isEqualTo(passwordBeforeAttack);
    }
}
