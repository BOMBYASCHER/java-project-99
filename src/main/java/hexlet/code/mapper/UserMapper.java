package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = JsonNullableMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeMapping
    public void encryptPassword(UserCreateDTO createDTO) {
        var password = createDTO.getPassword();
        var encryptedPassword = passwordEncoder.encode(password);
        createDTO.setPassword(encryptedPassword);
    }

    @BeforeMapping
    public void encryptPassword(UserUpdateDTO updateDTO) {
        if (updateDTO.getPassword() != null) {
            var password = updateDTO.getPassword().get();
            var encryptedPassword = JsonNullable.of(passwordEncoder.encode(password));
            updateDTO.setPassword(encryptedPassword);
        }
    }

    public abstract User map(UserCreateDTO createDTO);
    public abstract UserDTO map(User user);
    public abstract void update(UserUpdateDTO updateDTO, @MappingTarget User user);
}
