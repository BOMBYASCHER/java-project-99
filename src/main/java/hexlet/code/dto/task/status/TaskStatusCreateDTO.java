package hexlet.code.dto.task.status;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {
    @Size(min = 1)
    private String name;

    @Size(min = 1)
    @Pattern(regexp = "^[a-z0-9]+(?:([-_])[a-z0-9]+)*$")
    private String slug;
}
