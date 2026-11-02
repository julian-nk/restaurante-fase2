package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.GetPersonsUseCase;

import java.util.List;

public class GetPersonsUseCaseImpl implements GetPersonsUseCase {

    private final PersonGateway personGateway;

    public GetPersonsUseCaseImpl(PersonGateway personGateway) {
        this.personGateway = personGateway;
    }

    @Override
    public List<PersonOutputDto> execute() {
        return personGateway.findAll().stream().map(PersonOutputDto::from).toList();
    }
}
