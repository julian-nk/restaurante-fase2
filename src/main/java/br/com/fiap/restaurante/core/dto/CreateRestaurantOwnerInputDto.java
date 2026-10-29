package br.com.fiap.restaurante.core.dto;

/**
 * Dados do cadastro de dono de restaurante. Se já existir uma pessoa com esse CPF (por ser cliente),
 * ela é reaproveitada e seus dados são substituídos por estes.
 *
 * @param address pode ser nulo (pessoa sem endereço)
 */
public record CreateRestaurantOwnerInputDto(
        String name,
        String email,
        String cpf,
        AddressInputDto address,
        String businessEmail
) {
}
