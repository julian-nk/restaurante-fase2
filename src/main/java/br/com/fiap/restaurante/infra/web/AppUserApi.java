package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.AppUserController;
import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.infra.security.AppUserPrincipal;
import br.com.fiap.restaurante.infra.web.json.ChangePasswordJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuários",
        description = "Usuários de acesso ao sistema. Cada pessoa tem um único usuário, criado junto com ela no "
                + "cadastro de pessoa, de cliente ou de dono de restaurante.")
@RestController
@RequestMapping("/api/app-users")
public class AppUserApi {

    private final AppUserController appUserController;

    public AppUserApi(AppUserController appUserController) {
        this.appUserController = appUserController;
    }

    @GetMapping
    @Operation(summary = "Lista os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários cadastrados.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUserOutputDto.class))))
    })
    public ResponseEntity<List<AppUserOutputDto>> findAll() {
        return ResponseEntity.ok(appUserController.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário pelo ID")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado.",
                    content = @Content(schema = @Schema(implementation = AppUserOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<AppUserOutputDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appUserController.findById(id));
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Troca a senha do usuário",
            description = "Cada usuário só troca a própria senha: o ID precisa ser o do usuário logado, mesmo "
                    + "para o administrador. Exige a senha atual. A nova senha precisa ter de 8 a 72 caracteres, "
                    + "com letras e números.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nova senha fora da política."),
            @ApiResponse(responseCode = "403", description = "O ID não é o do usuário logado. A resposta é a "
                    + "mesma quando o usuário não existe, para não revelar quais usuários existem."),
            @ApiResponse(responseCode = "422", description = "A senha atual está incorreta.")
    })
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordJson json,
            @Parameter(hidden = true) Authentication authentication
    ) {
        appUserController.changePassword(json.toInputDto(id), AppUserPrincipal.requesterOf(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um usuário",
            description = "Os tipos associados ao usuário são removidos junto.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appUserController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
