package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.AppUserController;
import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.infra.web.json.ChangePasswordJson;
import br.com.fiap.restaurante.infra.web.json.CreateAppUserJson;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuários",
        description = "Usuários de acesso ao sistema. Cada pessoa pode ter um único usuário.")
@RestController
@RequestMapping("/api/app-users")
public class AppUserApi {

    private final AppUserController appUserController;

    public AppUserApi(AppUserController appUserController) {
        this.appUserController = appUserController;
    }

    @PostMapping
    @Operation(summary = "Cria o usuário de uma pessoa",
            description = "A senha é guardada como hash e nunca é devolvida pela API.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado.",
                    content = @Content(schema = @Schema(implementation = AppUserOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou senha fora da política (de 8 a 72 caracteres, com letras e "
                        + "números)."),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada."),
            @ApiResponse(responseCode = "409", description = "O login já está em uso ou a pessoa já tem um usuário.")
    })
    public ResponseEntity<AppUserOutputDto> create(@Valid @RequestBody CreateAppUserJson json) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appUserController.create(json.toInputDto()));
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
            description = "Exige a senha atual. A nova senha precisa ter de 8 a 72 caracteres, com letras e "
                    + "números.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nova senha fora da política."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "422", description = "A senha atual está incorreta.")
    })
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordJson json) {
        appUserController.changePassword(json.toInputDto(id));
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
