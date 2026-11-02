package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;

import java.util.List;

public interface GetUserTypesByAppUserUseCase {

    List<UserTypeOutputDto> execute(Long appUserId);
}
