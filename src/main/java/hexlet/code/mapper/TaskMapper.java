package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(source = "assignee_id", target = "assignee")
    @Mapping(target = "status", qualifiedByName = "EntityStatus")
    public abstract Task map(TaskCreateDTO taskCreateDTO) throws ResourceNotFoundException;

    @Mapping(source = "assignee.id", target = "assignee_id")
    @Mapping(source = "status.name", target = "status")
    public abstract TaskDTO map(Task task);

    @Mapping(source = "assignee_id", target = "assignee")
    public abstract void update(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);

    @Named("EntityStatus")
    public TaskStatus fromStatusToEntityStatus(String status) throws ResourceNotFoundException {
        return taskStatusRepository.findByName(status)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with name '" + status + "' not found"));
    }
}
