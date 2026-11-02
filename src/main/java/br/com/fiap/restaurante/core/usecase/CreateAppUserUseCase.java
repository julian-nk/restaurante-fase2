package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;

public interface CreateAppUserUseCase {

    AppUserOutputDto execute(CreateAppUserInputDto input);
}
