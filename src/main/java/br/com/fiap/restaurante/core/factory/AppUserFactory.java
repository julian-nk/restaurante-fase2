package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;

import java.util.Set;

public interface AppUserFactory {

    AppUser create(CreateAppUserInputDto input, String passwordHash);

    AppUser restore(Long id, Long personId, String login, String passwordHash, Set<Long> userTypeIds);
}
