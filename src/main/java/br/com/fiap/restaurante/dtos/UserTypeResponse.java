package br.com.fiap.restaurante.dtos;

public record UserTypeResponse(
        Long id,
        String name,
        String code
) {}