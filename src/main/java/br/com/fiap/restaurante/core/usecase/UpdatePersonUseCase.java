package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.dto.UpdatePersonInputDto;

public interface UpdatePersonUseCase {

    PersonOutputDto execute(UpdatePersonInputDto input);
}
