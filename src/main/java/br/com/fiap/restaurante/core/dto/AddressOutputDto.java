package br.com.fiap.restaurante.core.dto;

import br.com.fiap.restaurante.core.domain.Address;

public record AddressOutputDto(
        Long id,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode
) {

    /** @return {@code null} quando não há endereço */
    public static AddressOutputDto from(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressOutputDto(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState().getValue(),
                address.getZipCode()
        );
    }
}
