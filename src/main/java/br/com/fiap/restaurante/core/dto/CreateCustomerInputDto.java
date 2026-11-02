package br.com.fiap.restaurante.core.dto;

public record CreateCustomerInputDto(String name, String email, String cpf, AddressInputDto address) {
}
