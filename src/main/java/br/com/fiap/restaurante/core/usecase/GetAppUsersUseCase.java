package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.AppUserOutputDto;

import java.util.List;

public interface GetAppUsersUseCase {

    List<AppUserOutputDto> execute();
}
