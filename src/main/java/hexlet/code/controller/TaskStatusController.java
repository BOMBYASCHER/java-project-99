package hexlet.code.controller;

import hexlet.code.dto.task.status.TaskStatusCreateDTO;
import hexlet.code.dto.task.status.TaskStatusDTO;
import hexlet.code.dto.task.status.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping
    ResponseEntity<List<TaskStatusDTO>> index() {
        var taskStatuses = taskStatusService.getAllTaskStatuses();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    @GetMapping("/{id}")
    TaskStatusDTO show(@PathVariable Long id)
            throws ResourceNotFoundException {
        return taskStatusService.getTaskStatus(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO taskStatusCreateDTO)
            throws ResourceAlreadyExistsException {
        return taskStatusService.createTaskStatus(taskStatusCreateDTO);
    }

    @PutMapping("/{id}")
    TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO taskStatusUpdateDTO, @PathVariable Long id)
            throws ResourceAlreadyExistsException, ResourceNotFoundException {
        return taskStatusService.updateTaskStatus(id, taskStatusUpdateDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) throws ResourceNotFoundException {
        taskStatusService.deleteTaskStatus(id);
    }
}
