package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Dados para cadastrar um restaurante.")
public record CreateRestaurantJson(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        @Schema(description = "Nome do restaurante.", example = "Cantina da Nona")
        String name,

        @NotBlank(message = "O CNPJ é obrigatório.")
        @Schema(description = "CNPJ (14 dígitos), com ou sem pontuação. Não pode ser alterado depois.", example = "11222333000181")
        String cnpj,

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

    public CreateRestaurantInputDto toInputDto() {
        return new CreateRestaurantInputDto(name, cnpj, cuisineType, ownerId,
                AddressJson.toInputDto(address), OpeningHoursJson.toInputDtos(openingHours));
    }
}
