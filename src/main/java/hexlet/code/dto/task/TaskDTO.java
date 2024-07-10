package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String title;
    private Long index;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
    //CHECKSTYLE:OFF
    private Long assignee_id;
    //CHECKSTYLE:ON
    private LocalDate createdAt;
}
