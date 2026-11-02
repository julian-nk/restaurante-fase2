package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.UpdateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;

public interface UpdateUserTypeUseCase {

    UserTypeOutputDto execute(UpdateUserTypeInputDto input);
}
