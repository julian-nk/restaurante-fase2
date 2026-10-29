package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.factory.AppUserFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    private final AppUserFactory appUserFactory;

    public AppUserMapper(AppUserFactory appUserFactory) {
        this.appUserFactory = appUserFactory;
    }

    public AppUser map(AppUserEntity entity) {
        return appUserFactory.restore(entity.getId(), entity.getPersonId(), entity.getLogin(), entity.getPasswordHash());
    }

    public AppUserEntity map(AppUser appUser) {
        return new AppUserEntity(appUser.getId(), appUser.getPersonId(), appUser.getLogin(), appUser.getPasswordHash());
    }
}
