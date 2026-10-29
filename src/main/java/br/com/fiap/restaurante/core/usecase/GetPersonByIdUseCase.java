package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.PersonOutputDto;

public interface GetPersonByIdUseCase {

    PersonOutputDto execute(Long id);
}
