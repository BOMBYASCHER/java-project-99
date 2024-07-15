package hexlet.code.util;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.dto.task.status.TaskStatusCreateDTO;
import hexlet.code.dto.task.status.TaskStatusUpdateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    private final Faker faker = new Faker();

    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateDTOModel;
    private Model<UserUpdateDTO> userUpdateDTOModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<TaskCreateDTO> taskCreateDTOModel;
    private Model<TaskUpdateDTO> taskUpdateDTOModel;
    private Model<TaskStatusCreateDTO> taskStatusCreateDTOModel;
    private Model<TaskStatusUpdateDTO> taskStatusUpdateDTOModel;
    private Model<Label> labelModel;
    private Model<LabelCreateDTO> labelCreateDTOModel;
    private Model<LabelUpdateDTO> labelUpdateDTOModel;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password(8, 16))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .toModel();
        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password(8, 16))
                .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .toModel();
        userUpdateDTOModel = Instancio.of(UserUpdateDTO.class)
                .supply(Select.field(UserUpdateDTO::getEmail), () -> JsonNullable.of(faker.internet().emailAddress()))
                .supply(Select.field(UserUpdateDTO::getPassword),
                        () -> JsonNullable.of(faker.internet().password(8, 16)))
                .supply(Select.field(UserUpdateDTO::getFirstName),
                        () -> JsonNullable.of(faker.name().firstName()))
                .supply(Select.field(UserUpdateDTO::getLastName),
                        () -> JsonNullable.of(faker.name().lastName()))
                .toModel();
        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName),
                        () -> faker.lorem().word() + '_' + faker.lorem().word() + '_' + faker.lorem().word())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
                .toModel();
        taskStatusCreateDTOModel = Instancio.of(TaskStatusCreateDTO.class)
                .supply(Select.field(TaskStatusCreateDTO::getName),
                        () -> faker.lorem().word() + '_' + faker.lorem().word() + '_' + faker.lorem().word())
                .supply(Select.field(TaskStatusCreateDTO::getSlug), () -> faker.internet().slug())
                .toModel();
        taskStatusUpdateDTOModel = Instancio.of(TaskStatusUpdateDTO.class)
                .supply(Select.field(TaskStatusUpdateDTO::getName),
                        () -> faker.lorem().word() + '_' + faker.lorem().word() + '_' + faker.lorem().word())
                .supply(Select.field(TaskStatusUpdateDTO::getSlug), () -> faker.internet().slug())
                .toModel();
        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getStatus))
                .ignore(Select.field(Task::getLabels))
                .supply(Select.field(Task::getTitle),
                        () -> faker.lorem().word() + '_' + faker.lorem().word() + '_' + faker.lorem().word())
                .supply(Select.field(Task::getContent), () -> faker.lorem().paragraph(4))
                .supply(Select.field(Task::getIndex), () -> faker.number().numberBetween(200L, 1000L))
                .toModel();
        taskCreateDTOModel = Instancio.of(TaskCreateDTO.class)
                .ignore(Select.field(TaskCreateDTO::getAssignee_id))
                .ignore(Select.field(TaskCreateDTO::getStatus))
                .ignore(Select.field(TaskCreateDTO::getTaskLabelIds))
                .supply(Select.field(TaskCreateDTO::getTitle),
                        () -> faker.lorem().word() + '_' + faker.lorem().word() + '_' + faker.lorem().word())
                .supply(Select.field(TaskCreateDTO::getContent),
                        () -> JsonNullable.of(faker.lorem().paragraph(4)))
                .supply(Select.field(TaskCreateDTO::getIndex),
                        () -> JsonNullable.of(faker.number().numberBetween(1200L, 2000L)))
                .toModel();
        taskUpdateDTOModel = Instancio.of(TaskUpdateDTO.class)
                .ignore(Select.field(TaskUpdateDTO::getAssignee_id))
                .ignore(Select.field(TaskUpdateDTO::getStatus))
                .ignore(Select.field(TaskUpdateDTO::getTaskLabelIds))
                .supply(Select.field(TaskUpdateDTO::getTitle),
                        () -> JsonNullable.of(
                                faker.lorem().word() + '_' + faker.lorem().word() + '_' + faker.lorem().word()
                        ))
                .supply(Select.field(TaskUpdateDTO::getContent),
                        () -> JsonNullable.of(faker.lorem().paragraph(4)))
                .supply(Select.field(TaskUpdateDTO::getIndex),
                        () -> JsonNullable.of(faker.number().numberBetween(1200L, 2000L)))
                .toModel();
        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getTasks))
                .supply(Select.field(Label::getName), () -> faker.text().text(3, 1000))
                .toModel();
        labelCreateDTOModel = Instancio.of(LabelCreateDTO.class)
                .supply(Select.field(LabelCreateDTO::getName), () -> faker.text().text(3, 1000))
                .toModel();
        labelUpdateDTOModel = Instancio.of(LabelUpdateDTO.class)
                .supply(Select.field(LabelUpdateDTO::getName), () -> faker.text().text(3, 1000))
                .toModel();
    }
}
