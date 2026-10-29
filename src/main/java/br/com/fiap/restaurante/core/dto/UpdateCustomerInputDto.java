package br.com.fiap.restaurante.core.dto;

/**
 * @param address quando nulo, o endereço atual da pessoa é removido
 */
public record UpdateCustomerInputDto(Long id, String name, String email, AddressInputDto address) {
}
