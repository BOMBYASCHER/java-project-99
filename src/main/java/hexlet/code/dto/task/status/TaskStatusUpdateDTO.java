package hexlet.code.dto.task.status;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {
    @Size(min = 1)
    private JsonNullable<String> name;

    @Size(min = 1)
    @Pattern(regexp = "^\\w+(?:([-_])[a-z0-9]+)*$")
    private JsonNullable<String> slug;
}
