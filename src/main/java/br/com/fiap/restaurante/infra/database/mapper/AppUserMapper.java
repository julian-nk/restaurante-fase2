package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.factory.AppUserFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class AppUserMapper {

    private final AppUserFactory appUserFactory;

    public AppUserMapper(AppUserFactory appUserFactory) {
        this.appUserFactory = appUserFactory;
    }

    /** @param userTypeIds ids dos tipos do usuário, lidos da tabela app_user_type */
    public AppUser map(AppUserEntity entity, Collection<Long> userTypeIds) {
        return appUserFactory.restore(entity.getId(), entity.getPersonId(), entity.getLogin(), entity.getPasswordHash(),
                                      new HashSet<>(userTypeIds));
    }

    /** Só os dados da tabela app_user; os tipos são gravados à parte, em app_user_type. */
    public AppUserEntity map(AppUser appUser) {
        return new AppUserEntity(appUser.getId(), appUser.getPersonId(), appUser.getLogin(), appUser.getPasswordHash());
    }
}
