package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar um cliente. Se o CPF já pertencer a uma pessoa "
                      + "(com ou sem outro papel), ela passa a ser também cliente e os dados dela são substituídos por estes.")
public record RegisterCustomerJson(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        @Schema(description = "Nome completo.", example = "Maria da Silva")
        String name,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail é inválido.")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
        @Schema(description = "E-mail. Não pode se repetir entre as pessoas cadastradas.", example = "maria.silva@email.com")
        String email,

        @NotBlank(message = "O CPF é obrigatório.")
        @Schema(description = "CPF (11 dígitos), com ou sem pontuação. Não pode ser alterado depois.", example = "52998224725")
        String cpf,

        @Valid
        @Schema(description = "Endereço (opcional).")
        AddressJson address
) {

    public CreateCustomerInputDto toInputDto() {
        return new CreateCustomerInputDto(name, email, cpf, AddressJson.toInputDto(address));
    }
}
