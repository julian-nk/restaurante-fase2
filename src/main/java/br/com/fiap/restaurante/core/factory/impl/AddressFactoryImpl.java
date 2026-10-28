package br.com.fiap.restaurante.core.factory.impl;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.factory.AddressFactory;

public class AddressFactoryImpl implements AddressFactory {

    @Override
    public Address create(AddressInputDto input) {
        if (input == null) {
            throw new InvalidFieldException("O campo endereço é obrigatório.");
        }

        return Address.builder()
                .street(input.street())
                .number(input.number())
                .complement(input.complement())
                .neighborhood(input.neighborhood())
                .city(input.city())
                .state(input.state())
                .zipCode(input.zipCode())
                .build();
    }

    @Override
    public Address restore(
            Long id,
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String zipCode
    ) {
        return Address.builder()
                .id(id)
                .street(street)
                .number(number)
                .complement(complement)
                .neighborhood(neighborhood)
                .city(city)
                .state(state == null ? null : StatesEnum.fromValue(state))
                .zipCode(zipCode)
                .build();
    }
}
