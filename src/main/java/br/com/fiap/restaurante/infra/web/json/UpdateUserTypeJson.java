package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.UpdateUserTypeInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Novos dados do tipo de usuário.")
public record UpdateUserTypeJson(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
        @Schema(description = "Nome do tipo.", example = "Cliente")
        String name,

        @NotBlank(message = "O código é obrigatório")
        @Size(max = 30, message = "O código deve ter no máximo 30 caracteres")
        @Schema(description = "Código único que identifica o tipo.", example = "customer")
        String code
) {

    public UpdateUserTypeInputDto toInputDto(Long id) {
        return new UpdateUserTypeInputDto(id, name, code);
    }
}
