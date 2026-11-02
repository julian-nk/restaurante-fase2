package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.PersonOutputDto;

import java.util.List;

public interface GetPersonsUseCase {

    List<PersonOutputDto> execute();
}
