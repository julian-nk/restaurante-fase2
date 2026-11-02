package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.UpdateCustomerInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Novos dados do cliente. Substitui todos os campos; o CPF não pode ser alterado.")
public record UpdateCustomerJson(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        @Schema(description = "Nome completo.", example = "Maria da Silva")
        String name,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail é inválido.")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
        @Schema(description = "E-mail. Não pode se repetir entre as pessoas cadastradas.", example = "maria.silva@email.com")
        String email,

        @Valid
        @Schema(description = "Endereço. Se for omitido, o endereço atual é removido.")
        AddressJson address
) {

    public UpdateCustomerInputDto toInputDto(Long id) {
        return new UpdateCustomerInputDto(id, name, email, AddressJson.toInputDto(address));
    }
}
