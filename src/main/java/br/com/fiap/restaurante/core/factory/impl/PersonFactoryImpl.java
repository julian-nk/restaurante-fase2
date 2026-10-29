package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.core.factory.PersonFactory;

public class PersonFactoryImpl implements PersonFactory {

    private final AddressFactory addressFactory;

    public PersonFactoryImpl(AddressFactory addressFactory) {
        this.addressFactory = addressFactory;
    }

    @Override
    public Person create(CreatePersonInputDto input) {
        return Person.personBuilder()
                .name(input.name())
                .email(input.email())
                .cpf(input.cpf())
                .address(input.address() == null ? null : addressFactory.create(input.address()))
                .build();
    }

    @Override
    public Person restore(Long id, String name, String email, String cpf, Address address) {
        return Person.personBuilder()
                .id(id)
                .name(name)
                .email(email)
                .cpf(cpf)
                .address(address)
                .build();
    }
}
