package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.StatesEnum;

public record AddressInputDto(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        StatesEnum state,
        String zipCode
) {
}
