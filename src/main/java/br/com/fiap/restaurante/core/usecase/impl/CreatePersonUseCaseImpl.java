package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.exception.CpfAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.factory.PersonFactory;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.CreatePersonUseCase;

/**
 * Cadastra uma pessoa sem papel. O CPF e o e-mail não podem pertencer a outra pessoa. Para dar um papel a
 * uma pessoa que já existe, o cadastro de cliente ou de dono de restaurante é feito com o mesmo CPF.
 */
public class CreatePersonUseCaseImpl implements CreatePersonUseCase {

    private final PersonGateway personGateway;
    private final PersonFactory personFactory;

    public CreatePersonUseCaseImpl(PersonGateway personGateway, PersonFactory personFactory) {
        this.personGateway = personGateway;
        this.personFactory = personFactory;
    }

    @Override
    public PersonOutputDto execute(CreatePersonInputDto input) {
        String cpf = Person.normalizeCpf(input.cpf());
        if (personGateway.findIdByCpf(cpf).isPresent()) {
            throw new CpfAlreadyRegisteredException(cpf);
        }
        if (personGateway.isEmailUsedByAnotherPerson(input.email(), null)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }
        return PersonOutputDto.from(personGateway.save(personFactory.create(input)));
    }
}
