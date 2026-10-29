package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.RestaurantController;
import br.com.fiap.restaurante.core.dto.RestaurantOutputDto;
import br.com.fiap.restaurante.infra.web.json.CreateRestaurantJson;
import br.com.fiap.restaurante.infra.web.json.UpdateRestaurantJson;
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

@Tag(name = "Restaurantes",
        description = "Cadastro de restaurantes, com endereço e horários de funcionamento.")
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantApi {

    private final RestaurantController restaurantController;

    public RestaurantApi(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @PostMapping
    @Operation(summary = "Cadastra um restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante cadastrado.",
                    content = @Content(schema = @Schema(implementation = RestaurantOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horários de funcionamento sobrepostos."),
            @ApiResponse(responseCode = "404", description = "Dono de restaurante não encontrado."),
            @ApiResponse(responseCode = "409", description = "Já existe um restaurante com esse CNPJ.")
    })
    public ResponseEntity<RestaurantOutputDto> create(@Valid @RequestBody CreateRestaurantJson json) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantController.create(json.toInputDto()));
    }

    @GetMapping
    @Operation(summary = "Lista os restaurantes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurantes cadastrados.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantOutputDto.class))))
    })
    public ResponseEntity<List<RestaurantOutputDto>> findAll() {
        return ResponseEntity.ok(restaurantController.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um restaurante pelo ID")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do restaurante", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado.",
                    content = @Content(schema = @Schema(implementation = RestaurantOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    public ResponseEntity<RestaurantOutputDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantController.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um restaurante",
            description = "Substitui todos os dados, inclusive endereço e horários de funcionamento. O CNPJ "
                    + "não pode ser alterado.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do restaurante", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado.",
                    content = @Content(schema = @Schema(implementation = RestaurantOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horários de funcionamento sobrepostos."),
            @ApiResponse(responseCode = "404", description = "Restaurante ou dono de restaurante não encontrado.")
    })
    public ResponseEntity<RestaurantOutputDto> update(@PathVariable Long id, @Valid @RequestBody UpdateRestaurantJson json) {
        return ResponseEntity.ok(restaurantController.update(json.toInputDto(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um restaurante",
            description = "Os pratos e os horários de funcionamento do restaurante são excluídos junto.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do restaurante", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurante excluído."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restaurantController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
