package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Endereço.")
public record AddressJson(
        @NotBlank(message = "O logradouro é obrigatório.")
        @Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres.")
        @Schema(description = "Logradouro.", example = "Av. Paulista")
        String street,

        @NotBlank(message = "O número é obrigatório.")
        @Size(max = 10, message = "O número deve ter no máximo 10 caracteres.")
        @Schema(description = "Número.", example = "1000")
        String number,

        @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres.")
        @Schema(description = "Complemento (opcional).", example = "Sala 12")
        String complement,

        @NotBlank(message = "O bairro é obrigatório.")
        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres.")
        @Schema(description = "Bairro.", example = "Bela Vista")
        String neighborhood,

        @NotBlank(message = "A cidade é obrigatória.")
        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
        @Schema(description = "Cidade.", example = "São Paulo")
        String city,

        @NotBlank(message = "A UF é obrigatória.")
        @Pattern(regexp = "[A-Za-z]{2}", message = "A UF deve ter 2 letras.")
        @Schema(description = "UF, com 2 letras.", example = "SP")
        String state,

        @NotBlank(message = "O CEP é obrigatório.")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 00000-000 ou 00000000.")
        @Schema(description = "CEP, com ou sem hífen.", example = "01310-100")
        String zipCode
) {

    public AddressInputDto toInputDto() {
        return new AddressInputDto(street, number, complement, neighborhood, city, StatesEnum.fromValue(state), zipCode);
    }

    static AddressInputDto toInputDto(AddressJson json) {
        return json == null ? null : json.toInputDto();
    }
}
