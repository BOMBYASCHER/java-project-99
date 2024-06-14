package hexlet.code.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.status.TaskStatusCreateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
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
public class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    private TaskStatus taskStatus;
    private TaskStatusCreateDTO taskStatusCreateDTO;

    @BeforeEach
    void setUp() {
        taskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusCreateDTO = new TaskStatusCreateDTO();
        taskStatusCreateDTO.setName(taskStatus.getName());
        taskStatusCreateDTO.setSlug(taskStatus.getSlug());
    }

    @Test
    void testIndex() throws Exception {
        var taskStatuses = taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
        var request = get("/api/task_statuses");
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isEqualTo(om.writeValueAsString(taskStatuses));
    }

    @Test
    void testShow() throws Exception {
        taskStatusRepository.save(taskStatus);
        var request = get("/api/task_statuses/" + taskStatus.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isEqualTo(om.writeValueAsString(taskStatus));
    }

    @Test
    void testCreate() throws Exception {
        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusCreateDTO))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var savedTaskStatus = taskStatusRepository.findBySlug(taskStatusCreateDTO.getSlug()).get();
        assertThat(savedTaskStatus).isNotNull();
        assertThat(taskStatusCreateDTO.getSlug()).isEqualTo(savedTaskStatus.getSlug());
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        var badSlug = "raN+Sy(_)";
        taskStatusCreateDTO.setSlug(badSlug);
        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusCreateDTO))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        var taskStatusThatShouldNotExist = taskStatusRepository.findBySlug(badSlug);
        assertThat(taskStatusThatShouldNotExist.isEmpty()).isTrue();
    }

    @Test
    void testCreateWithAlreadyExistingData() throws Exception {
        var slug = "draft";
        taskStatusCreateDTO.setSlug(slug);
        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusCreateDTO))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isConflict());
        var draftTaskStatus = taskStatusRepository.findBySlug(slug).get();
        assertThat(draftTaskStatus.getName()).isNotEqualTo(taskStatusCreateDTO.getName());
    }

    @Test
    void testUpdate() throws Exception {
        taskStatusRepository.save(taskStatus);
        var name = "new-name";
        var update = Map.of("name", name);
        var request = put("/api/task_statuses/" + taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var updatedTaskStatus = taskStatusRepository.findById(taskStatus.getId()).get();
        assertThat(updatedTaskStatus.getName()).isEqualTo(name);
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        taskStatusRepository.save(taskStatus);
        var badSlug = "raN+Sy(_)";
        var update = Map.of("slug", badSlug);
        var request = put("/api/task_statuses/" + taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        var updatedTaskStatus = taskStatusRepository.findById(taskStatus.getId()).get();
        assertThat(updatedTaskStatus.getSlug()).isNotEqualTo(badSlug);
    }

    @Test
    void testUpdateWithAlreadyExistingData() throws Exception {
        taskStatusRepository.save(taskStatus);
        var slug = "draft";
        var update = Map.of("slug", slug);
        var request = put("/api/task_statuses/" + taskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(update))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isConflict());
        var updatedTaskStatus = taskStatusRepository.findById(taskStatus.getId()).get();
        assertThat(updatedTaskStatus.getSlug()).isNotEqualTo(slug);
    }

    @Test
    void testDelete() throws Exception {
        taskStatusRepository.save(taskStatus);
        var request = delete("/api/task_statuses/" + taskStatus.getId())
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        var deletedTaskStatus = taskStatusRepository.findById(taskStatus.getId());
        assertThat(deletedTaskStatus.isPresent()).isFalse();
    }

    @Test
    void testDeleteNonExistingTaskStatus() throws Exception {
        var request = delete("/api/task_statuses/" + 9000)
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}
