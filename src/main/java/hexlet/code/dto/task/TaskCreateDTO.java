package hexlet.code.dto.task;

import hexlet.code.validate.Exists;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    @Size(min = 1)
    private String title;
    private JsonNullable<Long> index;
    private JsonNullable<String> content;
    @NotNull
    @Exists
    private String status;
    @Exists
    //CHECKSTYLE:OFF
    private JsonNullable<Long> assignee_id;
    //CHECKSTYLE:ON
    private JsonNullable<Set<Long>> taskLabelIds;
}
