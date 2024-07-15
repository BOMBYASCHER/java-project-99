package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
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
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(source = "assignee_id", target = "assignee")
    @Mapping(target = "status", qualifiedByName = "EntityStatus")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "TaskLabels")
    public abstract Task map(TaskCreateDTO taskCreateDTO) throws ResourceNotFoundException;

    @Mapping(source = "assignee.id", target = "assignee_id")
    @Mapping(source = "status.slug", target = "status")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "LabelsIds")
    public abstract TaskDTO map(Task task);

    @Mapping(source = "assignee_id", target = "assignee")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "TaskLabels")
    public abstract void update(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);

    @Named("EntityStatus")
    public TaskStatus fromStatusToEntityStatus(String status) throws ResourceNotFoundException {
        return taskStatusRepository.findBySlug(status)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with slug '" + status + "' not found"));
    }

    @Named("TaskLabels")
    public Set<Label> fromIdToLabel(JsonNullable<Set<Long>> taskLabelIds) {
        Set<Label> labels = new HashSet<>();
        if (taskLabelIds.isPresent()) {
            for (Long id : taskLabelIds.get()) {
                labels.add(map(id));
            }
        }
        return labels;
    }
    public abstract Label map(Long id);

    @Named("LabelsIds")
    public Set<Long> labelsIds(Set<Label> labels) {
        Set<Long> ids = new HashSet<>();
        if (labels != null) {
            for (Label label : labels) {
                ids.add(label.getId());
            }
        }
        return ids;
    }
}
