package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.UserTypeController;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.infra.web.json.CreateUserTypeJson;
import br.com.fiap.restaurante.infra.web.json.UpdateUserTypeJson;
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

@Tag(name = "Tipos de usuário",
        description = "Cadastro dos tipos de usuário, como cliente e dono de restaurante. Um usuário "
                + "pode ter vários tipos.")
@RestController
@RequestMapping("/api/user-types")
public class UserTypeApi {

    private final UserTypeController userTypeController;

    public UserTypeApi(UserTypeController userTypeController) {
        this.userTypeController = userTypeController;
    }

    @PostMapping
    @Operation(summary = "Cadastra um tipo de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de usuário cadastrado.",
                    content = @Content(schema = @Schema(implementation = UserTypeOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "409", description = "Já existe um tipo de usuário com esse código.")
    })
    public ResponseEntity<UserTypeOutputDto> create(@Valid @RequestBody CreateUserTypeJson json) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userTypeController.create(json.toInputDto()));
    }

    @GetMapping
    @Operation(summary = "Lista os tipos de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipos de usuário cadastrados.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserTypeOutputDto.class))))
    })
    public ResponseEntity<List<UserTypeOutputDto>> findAll() {
        return ResponseEntity.ok(userTypeController.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um tipo de usuário pelo ID")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do tipo de usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de usuário encontrado.",
                    content = @Content(schema = @Schema(implementation = UserTypeOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado.")
    })
    public ResponseEntity<UserTypeOutputDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userTypeController.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de usuário",
            description = "Substitui o nome e o código do tipo.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do tipo de usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de usuário atualizado.",
                    content = @Content(schema = @Schema(implementation = UserTypeOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado."),
            @ApiResponse(responseCode = "409", description = "O código informado já pertence a outro tipo de usuário.")
    })
    public ResponseEntity<UserTypeOutputDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserTypeJson json
    ) {
        return ResponseEntity.ok(userTypeController.update(json.toInputDto(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um tipo de usuário")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do tipo de usuário", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de usuário excluído."),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userTypeController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
