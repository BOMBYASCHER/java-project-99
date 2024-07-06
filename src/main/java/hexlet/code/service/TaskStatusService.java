package hexlet.code.service;

import hexlet.code.dto.task.status.TaskStatusCreateDTO;
import hexlet.code.dto.task.status.TaskStatusDTO;
import hexlet.code.dto.task.status.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAllTaskStatuses() {
        return taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO getTaskStatus(Long taskStatusId)
            throws ResourceNotFoundException {
        var taskStatus = taskStatusRepository.findById(taskStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + taskStatusId + " not found"));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO createTaskStatus(TaskStatusCreateDTO statusCreateDTO)
            throws ResourceAlreadyExistsException {
        var taskStatus = taskStatusMapper.map(statusCreateDTO);
        try {
            taskStatusRepository.save(taskStatus);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException(e.getMessage());
        }
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO updateTaskStatus(Long taskStatusId, TaskStatusUpdateDTO statusUpdateDTO)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        var taskStatus = taskStatusRepository.findById(taskStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + taskStatusId + " not found"));
        try {
            taskStatusMapper.update(statusUpdateDTO, taskStatus);
            taskStatusRepository.save(taskStatus);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException(e.getMessage());
        }
        return taskStatusMapper.map(taskStatus);
    }

    public void deleteTaskStatus(Long taskStatusId)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        var taskStatus = taskStatusRepository.findById(taskStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + taskStatusId + " not found"));
        try {
            taskStatusRepository.delete(taskStatus);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException(
                    "Cannot delete a task status with id " + taskStatusId + " because it is bind with another resource"
            );
        }
    }
}
