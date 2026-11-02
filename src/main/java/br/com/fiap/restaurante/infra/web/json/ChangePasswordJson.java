package br.com.fiap.restaurante.infra.web.json;

import br.com.fiap.restaurante.core.dto.ChangePasswordInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Troca de senha do usuário.")
public record ChangePasswordJson(
        @NotBlank(message = "A senha atual é obrigatória.")
        @Schema(description = "Senha atual, para confirmar que é o próprio usuário.", example = "senhaAtual123")
        String currentPassword,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Schema(description = "Nova senha: de 8 a 72 caracteres, com letras e números.", example = "novaSenha456")
        String newPassword
) {

    public ChangePasswordInputDto toInputDto(Long appUserId) {
        return new ChangePasswordInputDto(appUserId, currentPassword, newPassword);
    }
}
