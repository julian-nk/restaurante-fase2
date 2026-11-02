package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;

public interface CreateUserTypeUseCase {

    UserTypeOutputDto execute(CreateUserTypeInputDto input);
}
