package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.task.status.TaskStatusCreateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private LabelService labelService;

    @Override
    public void run(ApplicationArguments args) {
        initAdmin();
        initTaskStatuses();
        initLabels();
    }

    private void initAdmin() {
        try {
            var admin = new UserCreateDTO();
            admin.setEmail("hexlet@example.com");
            admin.setPassword("qwerty");
            userService.create(admin);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void initTaskStatuses() {
        try {
            var taskStatusDraft = new TaskStatusCreateDTO();
            taskStatusDraft.setName("draft");
            taskStatusDraft.setSlug("draft");
            taskStatusService.createTaskStatus(taskStatusDraft);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            var taskStatusToReview = new TaskStatusCreateDTO();
            taskStatusToReview.setName("to_review");
            taskStatusToReview.setSlug("to_review");
            taskStatusService.createTaskStatus(taskStatusToReview);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            var taskStatusToBeFixed = new TaskStatusCreateDTO();
            taskStatusToBeFixed.setName("to_be_fixed");
            taskStatusToBeFixed.setSlug("to_be_fixed");
            taskStatusService.createTaskStatus(taskStatusToBeFixed);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            var taskStatusToPublish = new TaskStatusCreateDTO();
            taskStatusToPublish.setName("to_publish");
            taskStatusToPublish.setSlug("to_publish");
            taskStatusService.createTaskStatus(taskStatusToPublish);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            var taskStatusPublished = new TaskStatusCreateDTO();
            taskStatusPublished.setName("published");
            taskStatusPublished.setSlug("published");
            taskStatusService.createTaskStatus(taskStatusPublished);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void initLabels() {
        try {
            var label = new LabelCreateDTO();
            label.setName("feature");
            labelService.createLabel(label);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            var label = new LabelCreateDTO();
            label.setName("bug");
            labelService.createLabel(label);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
