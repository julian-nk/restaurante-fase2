package br.com.fiap.restaurante.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserTypeRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
        String name,

        @NotBlank(message = "O código é obrigatório")
        @Size(max = 30, message = "O código deve ter no máximo 30 caracteres")
        String code
) {}