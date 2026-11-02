package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.MenuItemController;
import br.com.fiap.restaurante.core.dto.MenuItemOutputDto;
import br.com.fiap.restaurante.infra.security.AppUserPrincipal;
import br.com.fiap.restaurante.infra.web.json.CreateMenuItemJson;
import br.com.fiap.restaurante.infra.web.json.UpdateMenuItemJson;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cardápio",
        description = "Pratos do cardápio dos restaurantes.")
@RestController
@RequestMapping("/api/menu-items")
public class MenuItemApi {

    private final MenuItemController menuItemController;

    public MenuItemApi(MenuItemController menuItemController) {
        this.menuItemController = menuItemController;
    }

    @PostMapping
    @Operation(summary = "Cadastra um prato")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prato cadastrado.",
                    content = @Content(schema = @Schema(implementation = MenuItemOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "403", description = "Sem permissão: o tipo do usuário não pode cadastrar "
                    + "pratos, ou o restaurante é de outro dono."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    public ResponseEntity<MenuItemOutputDto> create(
            @Valid @RequestBody CreateMenuItemJson json,
            @Parameter(hidden = true) Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(menuItemController.create(json.toInputDto(), AppUserPrincipal.requesterOf(authentication)));
    }

    @GetMapping
    @Operation(summary = "Lista todos os pratos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pratos cadastrados.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MenuItemOutputDto.class))))
    })
    public ResponseEntity<List<MenuItemOutputDto>> findAll() {
        return ResponseEntity.ok(menuItemController.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um prato pelo ID")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do prato", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prato encontrado.",
                    content = @Content(schema = @Schema(implementation = MenuItemOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado.")
    })
    public ResponseEntity<MenuItemOutputDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemController.findById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Lista os pratos de um restaurante",
            description = "Se o restaurante não tiver pratos, ou não existir, a lista vem vazia.")
    @Parameter(in = ParameterIn.PATH, name = "restaurantId", description = "ID do restaurante", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pratos do restaurante.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MenuItemOutputDto.class))))
    })
    public ResponseEntity<List<MenuItemOutputDto>> findByRestaurantId(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemController.findByRestaurantId(restaurantId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um prato",
            description = "Substitui todos os dados do prato. O restaurante não pode ser alterado.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do prato", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prato atualizado.",
                    content = @Content(schema = @Schema(implementation = MenuItemOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "403", description = "Sem permissão: o tipo do usuário não pode alterar "
                    + "pratos, ou o restaurante do prato é de outro dono."),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado.")
    })
    public ResponseEntity<MenuItemOutputDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemJson json,
            @Parameter(hidden = true) Authentication authentication
    ) {
        return ResponseEntity.ok(menuItemController.update(json.toInputDto(id),
                                                           AppUserPrincipal.requesterOf(authentication)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um prato")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do prato", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Prato excluído."),
            @ApiResponse(responseCode = "403", description = "Sem permissão: o tipo do usuário não pode excluir "
                    + "pratos, ou o restaurante do prato é de outro dono."),
            @ApiResponse(responseCode = "404", description = "Prato não encontrado.")
    })
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    ) {
        menuItemController.delete(id, AppUserPrincipal.requesterOf(authentication));
        return ResponseEntity.noContent().build();
    }
}
