package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.AppUserTypeInputDto;

public interface RemoveUserTypeFromAppUserUseCase {

    void execute(AppUserTypeInputDto input);
}
