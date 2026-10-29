package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.exception.CustomerAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.factory.CustomerFactory;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.RegisterCustomerUseCase;

/**
 * Cadastra uma pessoa como cliente. Se já existir uma pessoa com o CPF informado (porque ela é dona de
 * restaurante), a pessoa é reaproveitada: ela ganha o papel de cliente e seus dados (nome, e-mail e endereço)
 * são substituídos pelos enviados.
 */
public class RegisterCustomerUseCaseImpl implements RegisterCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final PersonGateway personGateway;
    private final CustomerFactory customerFactory;

    public RegisterCustomerUseCaseImpl(
            CustomerGateway customerGateway,
            PersonGateway personGateway,
            CustomerFactory customerFactory
    ) {
        this.customerGateway = customerGateway;
        this.personGateway = personGateway;
        this.customerFactory = customerFactory;
    }

    @Override
    public CustomerOutputDto execute(CreateCustomerInputDto input) {
        String cpf = Person.normalizeCpf(input.cpf());
        Long personId = personGateway.findIdByCpf(cpf).orElse(null);

        if (personId != null && customerGateway.existsById(personId)) {
            throw new CustomerAlreadyRegisteredException(personId);
        }
        if (personGateway.isEmailUsedByAnotherPerson(input.email(), personId)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        return CustomerOutputDto.from(customerGateway.save(customerFactory.create(personId, input)));
    }
}
