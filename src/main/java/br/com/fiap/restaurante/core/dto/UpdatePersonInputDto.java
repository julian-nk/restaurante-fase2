package br.com.fiap.restaurante.core.dto;

public record UpdatePersonInputDto(Long id, String name, String email, AddressInputDto address) {
}
