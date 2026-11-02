package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.core.factory.CustomerFactory;

import java.time.OffsetDateTime;

public class CustomerFactoryImpl implements CustomerFactory {

    private final AddressFactory addressFactory;

    public CustomerFactoryImpl(AddressFactory addressFactory) {
        this.addressFactory = addressFactory;
    }

    @Override
    public Customer create(Long personId, CreateCustomerInputDto input) {
        return Customer.builder()
                .id(personId)
                .name(input.name())
                .email(input.email())
                .cpf(input.cpf())
                .address(input.address() == null ? null : addressFactory.create(input.address()))
                .createdOn(OffsetDateTime.now())
                .build();
    }

    @Override
    public Customer restore(Long id, String name, String email, String cpf, Address address, OffsetDateTime createdOn) {
        return Customer.builder()
                .id(id)
                .name(name)
                .email(email)
                .cpf(cpf)
                .address(address)
                .createdOn(createdOn)
                .build();
    }
}
