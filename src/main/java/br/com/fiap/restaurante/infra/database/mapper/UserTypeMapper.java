package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.factory.UserTypeFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class UserTypeMapper {

    private final UserTypeFactory userTypeFactory;

    public UserTypeMapper(UserTypeFactory userTypeFactory) {
        this.userTypeFactory = userTypeFactory;
    }

    public UserType map(UserTypeEntity entity) {
        return userTypeFactory.restore(entity.getId(), entity.getName(), entity.getCode());
    }

    public UserTypeEntity map(UserType userType) {
        return new UserTypeEntity(userType.getId(), userType.getName(), userType.getCode());
    }
}
