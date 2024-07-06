package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO getTaskById(Long taskId)
            throws ResourceNotFoundException {
        return taskRepository.findById(taskId)
                .map(taskMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
    }

    public TaskDTO createTask(TaskCreateDTO taskCreateDTO)
            throws ResourceNotFoundException {
        Task task;
        try {
            task = taskMapper.map(taskCreateDTO);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO updateTask(Long taskId, TaskUpdateDTO taskUpdateDTO)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        try {
            taskMapper.update(taskUpdateDTO, task);
            taskRepository.save(task);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException(e.getMessage());
        }
        return taskMapper.map(task);
    }

    public void deleteTaskById(Long taskId)
            throws ResourceNotFoundException {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
        taskRepository.delete(task);
    }
}
