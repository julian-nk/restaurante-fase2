package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.exception.CpfAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.factory.PersonFactory;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.CreatePersonUseCase;
import br.com.fiap.restaurante.core.usecase.impl.PersonAppUserService.PendingAppUser;

import java.util.Set;

/**
 * Cadastra uma pessoa sem papel, junto com o usuário de acesso dela (sem tipos). O CPF e o e-mail não podem
 * pertencer a outra pessoa. Para dar um papel a uma pessoa que já existe, o cadastro de cliente ou de dono de
 * restaurante é feito com o mesmo CPF.
 */
public class CreatePersonUseCaseImpl implements CreatePersonUseCase {

    private final PersonGateway personGateway;
    private final PersonFactory personFactory;
    private final PersonAppUserService personAppUserService;

    public CreatePersonUseCaseImpl(
            PersonGateway personGateway,
            PersonFactory personFactory,
            PersonAppUserService personAppUserService
    ) {
        this.personGateway = personGateway;
        this.personFactory = personFactory;
        this.personAppUserService = personAppUserService;
    }

    @Override
    public PersonOutputDto execute(CreatePersonInputDto input, LoginInputDto login) {
        String cpf = Person.normalizeCpf(input.cpf());
        if (personGateway.findIdByCpf(cpf).isPresent()) {
            throw new CpfAlreadyRegisteredException(cpf);
        }
        if (personGateway.isEmailUsedByAnotherPerson(input.email(), null)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }
        Person person = personFactory.create(input);
        PendingAppUser appUser = personAppUserService.prepare(null, login, Set.of());

        Person saved = personGateway.save(person);
        appUser.save(saved.getId());
        return PersonOutputDto.from(saved);
    }
}
