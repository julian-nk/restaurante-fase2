package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastrar um prato no cardápio.")
public record CreateMenuItemJson(
        @NotNull(message = "O ID do restaurante é obrigatório.")
        @Positive(message = "O ID do restaurante deve ser maior que zero.")
        @Schema(description = "ID do restaurante.", example = "1")
        Long restaurantId,

        @NotBlank(message = "O nome do prato é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        @Schema(description = "Nome do prato.", example = "Feijoada completa")
        String name,

        @NotBlank(message = "A descrição do prato é obrigatória.")
        @Schema(description = "Descrição do prato.", example = "Feijoada com arroz, couve, farofa e laranja.")
        String description,

        @NotNull(message = "O preço é obrigatório.")
        @DecimalMin(value = "0.00", message = "O preço deve ser maior ou igual a zero.")
        @Schema(description = "Preço, em reais.", example = "59.90")
        BigDecimal price,

        @Schema(description = "Se verdadeiro, o prato só é servido no restaurante (não vai para delivery). Opcional: se omitido, vale false.", example = "false")
        Boolean dineInOnly,

        @Size(max = 500, message = "A URL da foto deve ter no máximo 500 caracteres.")
        @Schema(description = "URL da foto do prato (opcional).", example = "https://exemplo.com/fotos/feijoada.jpg")
        String photoUrl
) {

    public CreateMenuItemInputDto toInputDto() {
        return new CreateMenuItemInputDto(restaurantId, name, description, price, Boolean.TRUE.equals(dineInOnly), photoUrl);
    }
}
