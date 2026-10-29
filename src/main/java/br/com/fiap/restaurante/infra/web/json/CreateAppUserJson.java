package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar o usuário de acesso de uma pessoa.")
public record CreateAppUserJson(
        @NotNull(message = "O ID da pessoa é obrigatório.")
        @Positive(message = "O ID da pessoa deve ser maior que zero.")
        @Schema(description = "ID da pessoa dona do usuário. Cada pessoa pode ter um único usuário.", example = "1")
        Long personId,

        @NotBlank(message = "O login é obrigatório.")
        @Size(min = 3, max = 50, message = "O login deve ter entre 3 e 50 caracteres.")
        @Schema(description = "Login de acesso, de 3 a 50 caracteres.", example = "maria.silva")
        String login,

        @NotBlank(message = "A senha é obrigatória.")
        @Schema(description = "Senha: de 8 a 72 caracteres, com letras e números. É guardada como hash.", example = "senha1234")
        String password
) {

    public CreateAppUserInputDto toInputDto() {
        return new CreateAppUserInputDto(personId, login, password);
    }
}
