package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelMapper labelMapper;

    private Label label;
    private LabelCreateDTO labelCreateDTO;
    private LabelUpdateDTO labelUpdateDTO;
    private Task task;

    @BeforeEach
    void setUp() {
        label = Instancio.of(modelGenerator.getLabelModel()).create();
        task = Instancio.of(modelGenerator.getTaskModel()).create();
        task.setStatus(taskStatusRepository.findAll().getFirst());
        taskRepository.save(task);
        labelCreateDTO = Instancio.of(modelGenerator.getLabelCreateDTOModel()).create();
        labelUpdateDTO = Instancio.of(modelGenerator.getLabelUpdateDTOModel()).create();
    }

    @Test
    void testIndex() throws Exception {
        var labels = labelRepository.findAll()
                .stream()
                .map(labelMapper::map)
                .toList();
        var request = get("/api/labels")
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isEqualTo(om.writeValueAsString(labels));
    }

    @Test
    void testShow() throws Exception {
        labelRepository.save(label);
        var request = get("/api/labels/" + label.getId())
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThatJson(response).isEqualTo(om.writeValueAsString(labelMapper.map(label)));
    }

    @Test
    void testShowNonExistingLabel() throws Exception {
        var request = get("/api/labels/" + 9000)
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelCreateDTO))
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var labelFromRepository = labelRepository.findByName(labelCreateDTO.getName());
        assertThat(labelFromRepository).isNotEmpty();
        assertThatJson(response).isEqualTo(om.writeValueAsString(labelMapper.map(labelFromRepository.get())));
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        labelCreateDTO.setName("0");
        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelCreateDTO))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        var labelFromRepository = labelRepository.findByName(labelCreateDTO.getName());
        assertThat(labelFromRepository).isEmpty();
    }

    @Test
    void testUpdate() throws Exception {
        labelRepository.save(label);
        var request = put("/api/labels/" + label.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelUpdateDTO))
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var labelFromRepository = labelRepository.findByName(labelUpdateDTO.getName());
        assertThat(labelFromRepository).isNotEmpty();
        assertThat(labelFromRepository.get().getName()).isEqualTo(labelUpdateDTO.getName());
        assertThatJson(response).isEqualTo(om.writeValueAsString(labelMapper.map(labelFromRepository.get())));
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        labelRepository.save(label);
        labelUpdateDTO.setName("0");
        var request = put("/api/labels/" + label.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelUpdateDTO))
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        var labelFromRepository = labelRepository.findByName(label.getName());
        assertThat(labelFromRepository).isNotEmpty();
        var labelByUpdatingName = labelRepository.findByName(labelUpdateDTO.getName());
        assertThat(labelByUpdatingName).isEmpty();
    }

    @Test
    void testDelete() throws Exception {
        labelRepository.save(label);
        var request = delete("/api/labels/" + label.getId())
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        var labelFromRepository = labelRepository.findByName(label.getName());
        assertThat(labelFromRepository).isEmpty();
    }

    @Test
    void testDeleteNonExistingLabel() throws Exception {
        assertThat(labelRepository.existsById(9000L)).isFalse();
        var request = delete("/api/labels/" + 9000)
                .with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletingLabelThroughAPIWhenItBindWithTask() throws Exception {
        label.addTask(task);
        labelRepository.save(label);
        var request = delete("/api/labels/" + label.getId())
                .with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var labelFromRepository = labelRepository.findByName(label.getName());
        assertThat(labelFromRepository).isNotEmpty();
        assertThat(response).contains("Cannot", "delete", "label");
    }

    @Test
    void testM2M() {
//        label.addTask(task);
//        labelRepository.save(label);
//        var savedLabel = labelRepository.findById(label.getId()).get();
//        var taskIdsOfLabel = label.getTasks().stream().map(Task::getId).toList();
//        var taskIdsOfSavedLabel = savedLabel.getTasks().stream().map(Task::getId).toList();
//        assertThat(taskIdsOfLabel).isNotEmpty().isEqualTo(taskIdsOfSavedLabel);
//        var taskFromRepository = taskRepository.findById(task.getId()).get();
//        assertThat(taskFromRepository.getLabels()).isNotEmpty();

        labelRepository.save(label);
        var labelId = label.getId();
        task.addLabel(label);
        var labelsIdsOfTaskBeforeSave = task.getLabels().stream().map(Label::getId).toList();
        taskRepository.save(task);
        var taskId = task.getId();
        var savedTask = taskRepository.findById(taskId).get();
        var labelsIdsOfSavedTasks = savedTask.getLabels().stream().map(Label::getId).toList();
        assertThat(labelsIdsOfSavedTasks).isNotEmpty().isEqualTo(labelsIdsOfTaskBeforeSave);
        var savedLabel = labelRepository.findById(labelId).get();
        var tasksIdsOfSavedLabel = savedLabel.getTasks().stream().map(Task::getId).toList();
        assertThat(tasksIdsOfSavedLabel).contains(taskId);
    }
}
