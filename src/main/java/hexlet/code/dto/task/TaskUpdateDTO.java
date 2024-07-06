package hexlet.code.dto.task;

import hexlet.code.validate.Exists;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    @Size(min = 1)
    private JsonNullable<String> title;
    private JsonNullable<Long> index;
    private JsonNullable<String> content;
    @NotNull
    @Exists
    private JsonNullable<String> status;
    @Exists
    //CHECKSTYLE:OFF
    private JsonNullable<Long> assignee_id;
    //CHECKSTYLE:ON
}
