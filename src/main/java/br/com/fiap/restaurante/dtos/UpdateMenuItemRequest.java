
package br.com.fiap.restaurante.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(
        @NotBlank(message = "O nome do prato é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String name,

        @NotBlank(message = "A descrição do prato é obrigatória.")
        String description,

        @NotNull(message = "O preço é obrigatório.")
        @DecimalMin(value = "0.00", message = "O preço deve ser maior ou igual a zero.")
        BigDecimal price,

        boolean dineInOnly,

        @Size(max = 500, message = "A URL da foto deve ter no máximo 500 caracteres.")
        String photoUrl
) {}
