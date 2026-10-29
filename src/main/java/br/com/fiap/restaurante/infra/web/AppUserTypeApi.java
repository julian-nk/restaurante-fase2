package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.AppUserTypeController;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tipos de um usuário",
        description = "Associação entre usuários e tipos de usuário.")
@RestController
@RequestMapping("/api/app-users/{appUserId}/types")
public class AppUserTypeApi {

    private final AppUserTypeController appUserTypeController;

    public AppUserTypeApi(AppUserTypeController appUserTypeController) {
        this.appUserTypeController = appUserTypeController;
    }

    @PostMapping("/{userTypeId}")
    @Operation(summary = "Associa um tipo ao usuário",
            description = "Associar um tipo que o usuário já tem não gera erro.")
    @Parameter(in = ParameterIn.PATH, name = "appUserId", description = "ID do usuário", example = "1")
    @Parameter(in = ParameterIn.PATH, name = "userTypeId", description = "ID do tipo de usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo associado ao usuário."),
            @ApiResponse(responseCode = "404", description = "Usuário ou tipo de usuário não encontrado.")
    })
    public ResponseEntity<Void> associate(@PathVariable Long appUserId, @PathVariable Long userTypeId) {
        appUserTypeController.associate(appUserId, userTypeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Lista os tipos do usuário")
    @Parameter(in = ParameterIn.PATH, name = "appUserId", description = "ID do usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipos associados ao usuário.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserTypeOutputDto.class)))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public ResponseEntity<List<UserTypeOutputDto>> findByAppUser(@PathVariable Long appUserId) {
        return ResponseEntity.ok(appUserTypeController.findUserTypes(appUserId));
    }

    @DeleteMapping("/{userTypeId}")
    @Operation(summary = "Remove um tipo do usuário",
            description = "Remover um tipo que o usuário não tem não gera erro.")
    @Parameter(in = ParameterIn.PATH, name = "appUserId", description = "ID do usuário", example = "1")
    @Parameter(in = ParameterIn.PATH, name = "userTypeId", description = "ID do tipo de usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo removido do usuário."),
            @ApiResponse(responseCode = "404", description = "Usuário ou tipo de usuário não encontrado.")
    })
    public ResponseEntity<Void> remove(@PathVariable Long appUserId, @PathVariable Long userTypeId) {
        appUserTypeController.remove(appUserId, userTypeId);
        return ResponseEntity.noContent().build();
    }
}
