package br.com.fiap.restaurante.core.dto;

/**
 * @param address quando nulo, o endereço atual da pessoa é removido
 */
public record UpdateRestaurantOwnerInputDto(
        Long id,
        String name,
        String email,
        AddressInputDto address,
        String businessEmail
) {
}
