package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;

import java.util.List;

public interface GetUserTypesUseCase {

    List<UserTypeOutputDto> execute();
}
