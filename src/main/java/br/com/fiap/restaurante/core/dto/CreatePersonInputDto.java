package br.com.fiap.restaurante.core.dto;

public record CreatePersonInputDto(String name, String email, String cpf, AddressInputDto address) {
}
