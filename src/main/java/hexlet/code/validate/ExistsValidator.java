package hexlet.code.validate;

import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ExistsValidator {
    public static class AssigneeId implements ConstraintValidator<Exists, Long> {
        @Autowired
        private UserRepository userRepository;

        @Override
        public boolean isValid(Long field, ConstraintValidatorContext constraintValidatorContext) {
            if (field == null) {
                return true;
            }
            return userRepository.existsById(field);
        }
    }

    public static class Status implements ConstraintValidator<Exists, String> {
        @Autowired
        private TaskStatusRepository taskStatusRepository;

        @Override
        public boolean isValid(String field, ConstraintValidatorContext constraintValidatorContext) {
            return taskStatusRepository.existsBySlug(field);
        }
    }
}

