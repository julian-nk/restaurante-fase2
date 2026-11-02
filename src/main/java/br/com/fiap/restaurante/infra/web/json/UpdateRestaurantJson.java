package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.UpdateRestaurantInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Substitui os dados do restaurante, inclusive endereço e horários (o CNPJ não pode ser alterado).
 */
@Schema(description = "Novos dados do restaurante. Substitui todos os campos, inclusive endereço e horários; o CNPJ não pode ser alterado.")
public record UpdateRestaurantJson(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        @Schema(description = "Nome do restaurante.", example = "Cantina da Nona")
        String name,

        @NotBlank(message = "O tipo de cozinha é obrigatório.")
        @Size(max = 50, message = "O tipo de cozinha deve ter no máximo 50 caracteres.")
        @Schema(description = "Tipo de cozinha.", example = "Italiana")
        String cuisineType,

        @NotNull(message = "O ID do dono é obrigatório.")
        @Positive(message = "O ID do dono deve ser maior que zero.")
        @Schema(description = "ID do dono do restaurante (o ID da pessoa cadastrada como dona).", example = "1")
        Long ownerId,

        @NotNull(message = "O endereço é obrigatório.")
        @Valid
        @Schema(description = "Endereço do restaurante.")
        AddressJson address,

        @NotEmpty(message = "Informe pelo menos um horário de funcionamento.")
        @Schema(description = "Horários de funcionamento. Informe pelo menos um; os horários não podem se sobrepor.")
        List<@Valid OpeningHoursJson> openingHours
) {

    public UpdateRestaurantInputDto toInputDto(Long id) {
        return new UpdateRestaurantInputDto(id, name, cuisineType, ownerId,
                AddressJson.toInputDto(address), OpeningHoursJson.toInputDtos(openingHours));
    }
}
