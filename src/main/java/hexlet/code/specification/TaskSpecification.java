package hexlet.code.specification;

import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO taskParamsDTO) {
        if (taskParamsDTO == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return withTitleCont(taskParamsDTO.getTitleCont())
                .and(withAssigneeId(taskParamsDTO.getAssigneeId()))
                .and(withStatus(taskParamsDTO.getStatus()))
                .and(withLabelId(taskParamsDTO.getLabelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) ->
                titleCont == null
                        ? cb.conjunction()
                        : cb.like(root.get("titleCont"), titleCont);
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("assigneeId"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) ->
                labelId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("labelId"), labelId);
    }
}
