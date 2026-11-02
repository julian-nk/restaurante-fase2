package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.exception.PersonNotFoundException;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.GetPersonByIdUseCase;

public class GetPersonByIdUseCaseImpl implements GetPersonByIdUseCase {

    private final PersonGateway personGateway;

    public GetPersonByIdUseCaseImpl(PersonGateway personGateway) {
        this.personGateway = personGateway;
    }

    @Override
    public PersonOutputDto execute(Long id) {
        return personGateway.findById(id)
                .map(PersonOutputDto::from)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }
}
