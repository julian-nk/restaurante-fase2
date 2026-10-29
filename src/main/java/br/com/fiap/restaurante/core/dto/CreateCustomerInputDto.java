package br.com.fiap.restaurante.core.dto;

/**
 * Dados do cadastro de cliente. Se já existir uma pessoa com esse CPF (por ser dona de restaurante),
 * ela é reaproveitada e seus dados são substituídos por estes.
 *
 * @param address pode ser nulo (pessoa sem endereço)
 */
public record CreateCustomerInputDto(String name, String email, String cpf, AddressInputDto address) {
}
