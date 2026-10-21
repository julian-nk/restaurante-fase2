package br.com.fiap.restaurante.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserTypeRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O código é obrigatório")
        String code
) {
}