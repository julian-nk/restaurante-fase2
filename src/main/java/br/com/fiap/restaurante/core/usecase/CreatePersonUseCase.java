package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;

public interface CreatePersonUseCase {

    PersonOutputDto execute(CreatePersonInputDto input);
}
