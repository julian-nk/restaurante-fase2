package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;

public interface GetUserTypeByIdUseCase {

    UserTypeOutputDto execute(Long id);
}
