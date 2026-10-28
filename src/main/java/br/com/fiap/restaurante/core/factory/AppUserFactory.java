package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;

public interface AppUserFactory {

    /** @param passwordHash hash da senha já gerado (o usuário nunca guarda a senha em texto puro) */
    AppUser create(CreateAppUserInputDto input, String passwordHash);

    AppUser restore(Long id, Long personId, String login, String passwordHash);
}
