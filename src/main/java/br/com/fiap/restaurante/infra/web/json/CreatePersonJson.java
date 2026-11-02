package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar uma pessoa sem papel (um administrador, por exemplo), junto com o "
                      + "usuário de acesso dela. Para que ela passe a ser cliente ou dona de restaurante, cadastre o "
                      + "papel com o mesmo CPF.")
public record CreatePersonJson(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        @Schema(description = "Nome completo.", example = "Ana Pereira")
        String name,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail é inválido.")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
        @Schema(description = "E-mail. Não pode se repetir entre as pessoas cadastradas.", example = "ana.pereira@email.com")
        String email,

        @NotBlank(message = "O CPF é obrigatório.")
        @Schema(description = "CPF (11 dígitos), com ou sem pontuação. Não pode se repetir nem ser alterado depois.",
                example = "39053344705")
        String cpf,

        @Valid
        @Schema(description = "Endereço (opcional).")
        AddressJson address,
        @NotBlank(message = "O login é obrigatório.")
        @Size(min = 3, max = 50, message = "O login deve ter entre 3 e 50 caracteres.")
        @Schema(description = "Login do usuário de acesso, que nasce junto com a pessoa: de 3 a 50 caracteres.",
                example = "ana.pereira")
        String login,
        @NotBlank(message = "A senha é obrigatória.")
        @Schema(description = "Senha do usuário: de 8 a 72 caracteres, com letras e números. É guardada como hash.",
                example = "senha1234")
        String password
) {

    public CreatePersonInputDto toInputDto() {
        return new CreatePersonInputDto(name, email, cpf, AddressJson.toInputDto(address));
    }

    public LoginInputDto toLoginDto() {
        return new LoginInputDto(login, password);
    }
}
