package hexlet.code.mapper;

import hexlet.code.model.BaseModel;
import jakarta.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReferenceMapper {
    @Autowired
    private EntityManager entityManager;

    public <T extends BaseModel> T toEntity(Long id, @TargetType Class<T> modelClass) {
        return id == null ? null : entityManager.find(modelClass, id);
    }
}
