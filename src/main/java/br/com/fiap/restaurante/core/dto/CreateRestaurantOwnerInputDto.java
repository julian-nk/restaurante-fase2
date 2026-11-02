package br.com.fiap.restaurante.core.dto;

public record CreateRestaurantOwnerInputDto(
        String name,
        String email,
        String cpf,
        AddressInputDto address,
        String businessEmail
) {
}
