package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.dto.UpdatePersonInputDto;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.PersonNotFoundException;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.UpdatePersonUseCase;

/**
 * Atualiza nome, e-mail e endereço de qualquer pessoa, tenha ela papel ou não. Os dados dos papéis (como o
 * e-mail comercial do dono de restaurante) não mudam aqui.
 */
public class UpdatePersonUseCaseImpl implements UpdatePersonUseCase {

    private final PersonGateway personGateway;
    private final AddressFactory addressFactory;

    public UpdatePersonUseCaseImpl(PersonGateway personGateway, AddressFactory addressFactory) {
        this.personGateway = personGateway;
        this.addressFactory = addressFactory;
    }

    @Override
    public PersonOutputDto execute(UpdatePersonInputDto input) {
        Person person = personGateway.findById(input.id())
                .orElseThrow(() -> new PersonNotFoundException(input.id()));

        if (personGateway.isEmailUsedByAnotherPerson(input.email(), person.getId())) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        person.updatePersonalData(input.name(), input.email(),
                input.address() == null ? null : addressFactory.create(input.address()));
        return PersonOutputDto.from(personGateway.save(person));
    }
}
