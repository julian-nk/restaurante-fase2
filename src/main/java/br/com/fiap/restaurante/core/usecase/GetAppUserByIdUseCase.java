package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.AppUserOutputDto;

public interface GetAppUserByIdUseCase {

    AppUserOutputDto execute(Long id);
}
