package br.com.fiap.restaurante.core.usecase.impl;

import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateCustomerInputDto;
import br.com.fiap.restaurante.core.exception.CustomerNotFoundException;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.usecase.UpdateCustomerUseCase;

public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final PersonGateway personGateway;
    private final AddressFactory addressFactory;

    public UpdateCustomerUseCaseImpl(
            CustomerGateway customerGateway,
            PersonGateway personGateway,
            AddressFactory addressFactory
    ) {
        this.customerGateway = customerGateway;
        this.personGateway = personGateway;
        this.addressFactory = addressFactory;
    }

    @Override
    public CustomerOutputDto execute(UpdateCustomerInputDto input) {
        Customer customer = customerGateway.findById(input.id())
                .orElseThrow(() -> new CustomerNotFoundException(input.id()));

        if (personGateway.isEmailUsedByAnotherPerson(input.email(), customer.getId())) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        customer.updatePersonalData(input.name(), input.email(),
                input.address() == null ? null : addressFactory.create(input.address()));
        return CustomerOutputDto.from(customerGateway.save(customer));
    }
}
