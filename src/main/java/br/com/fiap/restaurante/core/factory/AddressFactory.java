package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.dto.AddressInputDto;

public interface AddressFactory {

    Address create(AddressInputDto input);

    Address restore(
            Long id,
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String zipCode
    );
}
