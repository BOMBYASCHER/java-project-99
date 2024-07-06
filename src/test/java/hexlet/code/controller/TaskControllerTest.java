package hexlet.code.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
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
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private AuthenticationUtil authentication;

    private Task task;
    private User anotherUser;
    private TaskCreateDTO taskCreateDTO;

    @BeforeEach
    void setUp() {
        task = Instancio.of(modelGenerator.getTaskModel()).create();
        anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        taskCreateDTO = Instancio.of(modelGenerator.getTaskCreateDTOModel()).create();
        var status = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var user = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(user);
        userRepository.save(anotherUser);
        taskStatusRepository.save(status);
        task.setAssignee(user);
        task.setStatus(status);
        taskCreateDTO.setStatus(status.getName());
        taskCreateDTO.setAssignee_id(JsonNullable.of(user.getId()));
    }

    @Test
    void testIndex() throws Exception {
        var tasks = taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
        var request = get("/api/tasks")
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isEqualTo(om.writeValueAsString(tasks));
    }

    @Test
    void testShow() throws Exception {
        taskRepository.save(task);
        var request = get("/api/tasks/" + task.getId())
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isNotNull().and(
                assertion -> assertion.node("assignee_id").isEqualTo(task.getAssignee().getId())
        );
    }

    @Test
    void testCreate() throws Exception {
        var content = om.writeValueAsString(taskCreateDTO);
        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwt());
        System.out.println(content);
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isNotNull().and(
                assertion -> assertion.node("assignee_id").isEqualTo(task.getAssignee().getId())
        );
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        taskCreateDTO.setTitle("");
        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO))
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).asString().isEmpty();
    }

    @Test
    void testUpdate() throws Exception {
        taskRepository.save(task);
        var update = Map.of("assignee_id", anotherUser.getId());
        var request = put("/api/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var taskFromRepository = taskRepository.findById(task.getId()).get();
        assertThat(taskFromRepository.getAssignee().getId()).isEqualTo(anotherUser.getId());
        assertThatJson(response).isNotNull().and(
                assertion -> assertion.node("assignee_id").isEqualTo(anotherUser.getId())
        );
    }

    @Test
    void testUpdateWithNoExistingAssignee() throws Exception {
        taskRepository.save(task);
        var update = Map.of("assignee_id", 9000L);
        var request = put("/api/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
        var taskFromRepository = taskRepository.findById(task.getId()).get();
        assertThat(taskFromRepository.getAssignee().getId()).isEqualTo(task.getAssignee().getId());
    }

    @Test
    void testDelete() throws Exception {
        taskRepository.save(task);
        var request = delete("/api/tasks/" + task.getId())
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var taskFromRepository = taskRepository.findById(task.getId());
        assertThat(taskFromRepository).isEmpty();
        assertThatJson(response).asString().isEmpty();
    }

    @Test
    void testDeleteNonExistingTask() throws Exception {
        var request = delete("/api/tasks/" + 19000L)
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(response).contains("Task", "not", "found", "19000");
    }

    @Test
    void testDeletingUserWhenItBindWithTask() throws Exception {
        taskRepository.save(task);
        var user = task.getAssignee();
        var userId = user.getId();
        String message;
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            message = e.getMessage();
        }
        var userFromRepository = userRepository.findById(userId);
        assertThat(userFromRepository).isPresent();
        assertThat(userFromRepository.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void testDeletingTaskStatusWhenItBindWithTask() throws Exception {
        taskRepository.save(task);
        var taskStatus = task.getStatus();
        var taskStatusId = taskStatus.getId();
        String message;
        try {
            taskStatusRepository.delete(taskStatus);
        } catch (Exception e) {
            message = e.getMessage();
        }
        var taskStatusFromRepository = taskStatusRepository.findById(taskStatusId);
        assertThat(taskStatusFromRepository).isPresent();
        assertThat(taskStatusFromRepository.get().getId()).isEqualTo(taskStatus.getId());
    }

    @Test
    void testDeletingUserThroughAPIWhenItBindWithTask() throws Exception {
        taskRepository.save(task);
        var user = task.getAssignee();
        var userId = user.getId();
        var token = authentication.generateBearerToken(user);
        var request = delete("/api/users/" + userId)
                .header(authentication.header(), token);
        var response = mockMvc.perform(request)
                .andExpect(status().isConflict());
        var userFromRepository = userRepository.findById(userId);
        assertThat(userFromRepository).isPresent();
        assertThat(userFromRepository.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void testDeletingTaskStatusThroughAPIWhenItBindWithTask() throws Exception {
        taskRepository.save(task);
        var taskStatus = task.getStatus();
        var taskStatusId = taskStatus.getId();
        var request = delete("/api/task_statuses/" + taskStatusId)
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isConflict());
        var taskStatusFromRepository = taskStatusRepository.findById(taskStatusId);
        assertThat(taskStatusFromRepository).isPresent();
        assertThat(taskStatusFromRepository.get().getId()).isEqualTo(taskStatus.getId());
    }
}
