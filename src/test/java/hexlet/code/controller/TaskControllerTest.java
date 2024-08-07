package hexlet.code.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
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

import java.util.Set;

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
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private AuthenticationUtil authentication;

    @Autowired
    private LabelRepository labelRepository;

    private Task task;
    private User anotherUser;
    private TaskCreateDTO taskCreateDTO;
    private TaskUpdateDTO taskUpdateDTO;

    @BeforeEach
    void setUp() {
        task = Instancio.of(modelGenerator.getTaskModel()).create();
        anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        taskCreateDTO = Instancio.of(modelGenerator.getTaskCreateDTOModel()).create();
        taskUpdateDTO = Instancio.of(modelGenerator.getTaskUpdateDTOModel()).create();
        var status = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var user = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(user);
        userRepository.save(anotherUser);
        taskStatusRepository.save(status);
        task.setAssignee(user);
        task.setStatus(status);
        taskCreateDTO.setStatus(status.getSlug());
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
    void testIndexWithParams() throws Exception {
        var assignee = userRepository.findById(1L).get();
        var toBeFixed = taskStatusRepository.findBySlug("to_be_fixed").get();
        var draft = taskStatusRepository.findBySlug("draft").get();
        var label = labelRepository.findById(1L).get();
        var task1 = Instancio.of(modelGenerator.getTaskModel()).create();
        task1.setStatus(toBeFixed);
        task1.setAssignee(assignee);
        task1.addLabel(label);
        task1.setTitle("create application");
        taskRepository.save(task1);
        var task2 = Instancio.of(modelGenerator.getTaskModel()).create();
        task2.setTitle("create");
        task2.setStatus(draft);
        taskRepository.save(task2);
        var task3 = Instancio.of(modelGenerator.getTaskModel()).create();
        task3.setAssignee(assignee);
        task3.setStatus(draft);
        taskRepository.save(task3);
        var task4 = Instancio.of(modelGenerator.getTaskModel()).create();
        task4.setStatus(toBeFixed);
        taskRepository.save(task4);
        var task5 = Instancio.of(modelGenerator.getTaskModel()).create();
        task5.addLabel(label);
        task5.setStatus(draft);
        taskRepository.save(task5);
        var requestAllParams = get("/api/tasks?titleCont=create&assigneeId=1&status=to_be_fixed&labelId=1")
                .with(jwt());
        var responseAllParams = mockMvc.perform(requestAllParams)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(responseAllParams).isArray().contains(om.writeValueAsString(taskMapper.map(task1)));
        var requestTitleContParam = get("/api/tasks?titleCont=create")
                .with(jwt());
        var responseTitleContParam = mockMvc.perform(requestTitleContParam)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(responseTitleContParam).isArray().contains(
                om.writeValueAsString(taskMapper.map(task1)),
                om.writeValueAsString(taskMapper.map(task2))
        );
        var requestAssigneeIdParam = get("/api/tasks?assigneeId=1")
                .with(jwt());
        var responseAssigneeIdParam = mockMvc.perform(requestAssigneeIdParam)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(responseAssigneeIdParam).isArray().contains(
                om.writeValueAsString(taskMapper.map(task1)),
                om.writeValueAsString(taskMapper.map(task3))
        );
        var requestStatusParam = get("/api/tasks?status=to_be_fixed")
                .with(jwt());
        var responseStatusParam = mockMvc.perform(requestStatusParam)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(responseStatusParam).isArray().contains(
                om.writeValueAsString(taskMapper.map(task1)),
                om.writeValueAsString(taskMapper.map(task4))
        );
        var requestLabelParam = get("/api/tasks?labelId=1")
                .with(jwt());
        var responseLabelParam = mockMvc.perform(requestLabelParam)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(responseLabelParam).isArray().contains(
                om.writeValueAsString(taskMapper.map(task1)),
                om.writeValueAsString(taskMapper.map(task5))
        );
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
        taskCreateDTO.setTaskLabelIds(JsonNullable.of(Set.of(1L, 2L)));
        var content = om.writeValueAsString(taskCreateDTO);
        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isNotNull().and(
                assertion -> assertion.node("assignee_id").isEqualTo(task.getAssignee().getId()),
                assertion -> assertion.node("taskLabelIds").isArray().contains(1L, 2L)
        );
        var taskId = om.readTree(response).get("id").asLong();
        var labelsOfSavedTask = taskRepository.findById(taskId).get().getLabels();
        var labelsIds = labelsOfSavedTask.stream().map(l -> l.getId());
        assertThat(labelsIds).contains(1L, 2L);
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
    void testCreateWithoutAssignee() throws Exception {
        taskCreateDTO.setAssignee_id(JsonNullable.of(null));
        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO))
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).and(
                assertion -> assertion.node("assignee_id").isAbsent()
        );
    }

    @Test
    void testUpdate() throws Exception {
        taskRepository.save(task);
        taskUpdateDTO.setTaskLabelIds(JsonNullable.of(Set.of(1L, 2L)));
        taskUpdateDTO.setAssignee_id(JsonNullable.of(anotherUser.getId()));
        var request = put("/api/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskUpdateDTO))
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var taskFromRepository = taskRepository.findById(task.getId()).get();
        assertThat(taskFromRepository.getAssignee().getId()).isEqualTo(anotherUser.getId());
        var labelsOfSavedTask = taskRepository.findById(task.getId()).get().getLabels();
        var labelsIds = labelsOfSavedTask.stream().map(l -> l.getId());
        assertThat(labelsIds).contains(1L, 2L);
        assertThatJson(response).isNotNull().and(
                assertion -> assertion.node("assignee_id").isEqualTo(anotherUser.getId()),
                assertion -> assertion.node("taskLabelIds").isArray().contains(1L, 2L)
        );
    }

    @Test
    void testUpdateWithNoExistingAssignee() throws Exception {
        taskRepository.save(task);
        taskUpdateDTO.setAssignee_id(JsonNullable.of(9000L));
        var request = put("/api/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskUpdateDTO))
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
