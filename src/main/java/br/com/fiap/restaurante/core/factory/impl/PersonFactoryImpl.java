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
        Address address = input.address() == null ? null : addressFactory.create(input.address());
        return new Person(null, input.name(), input.email(), input.cpf(), address);
    }

    @Override
    public Person restore(Long id, String name, String email, String cpf, Address address) {
        return new Person(id, name, email, cpf, address);
    }
}
