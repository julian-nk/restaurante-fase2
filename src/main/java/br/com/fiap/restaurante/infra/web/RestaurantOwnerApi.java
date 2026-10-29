package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.RestaurantOwnerController;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.infra.web.json.RegisterRestaurantOwnerJson;
import br.com.fiap.restaurante.infra.web.json.UpdateRestaurantOwnerJson;
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

@Tag(name = "Donos de restaurante",
        description = "Pessoas cadastradas como donas de restaurante. O dono é a própria pessoa: o ID dele é o "
                + "da pessoa.")
@RestController
@RequestMapping("/api/restaurant-owners")
public class RestaurantOwnerApi {

    private final RestaurantOwnerController restaurantOwnerController;

    public RestaurantOwnerApi(RestaurantOwnerController restaurantOwnerController) {
        this.restaurantOwnerController = restaurantOwnerController;
    }

    @PostMapping
    @Operation(summary = "Cadastra um dono de restaurante",
            description = "Cadastra a pessoa junto com o papel de dono. Se já existir uma pessoa com o CPF "
                    + "informado (cadastrada sem papel ou como cliente), ela passa a ser também dona, e o nome, "
                    + "o e-mail e o endereço dela são substituídos pelos enviados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dono de restaurante cadastrado.",
                    content = @Content(schema = @Schema(implementation = RestaurantOwnerOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "409",
                    description = "A pessoa já é dona de restaurante ou o e-mail pertence a outra pessoa.")
    })
    public ResponseEntity<RestaurantOwnerOutputDto> register(@Valid @RequestBody RegisterRestaurantOwnerJson json) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantOwnerController.register(json.toInputDto()));
    }

    @GetMapping
    @Operation(summary = "Lista os donos de restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donos de restaurante cadastrados.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantOwnerOutputDto.class))))
    })
    public ResponseEntity<List<RestaurantOwnerOutputDto>> findAll() {
        return ResponseEntity.ok(restaurantOwnerController.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um dono de restaurante pelo ID")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do dono (o mesmo da pessoa)", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dono de restaurante encontrado.",
                    content = @Content(schema = @Schema(implementation = RestaurantOwnerOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Dono de restaurante não encontrado.")
    })
    public ResponseEntity<RestaurantOwnerOutputDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantOwnerController.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um dono de restaurante",
            description = "Substitui nome, e-mail, endereço e e-mail comercial. O CPF não pode ser alterado; se o "
                    + "endereço for omitido, o endereço atual é removido.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do dono (o mesmo da pessoa)", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dono de restaurante atualizado.",
                    content = @Content(schema = @Schema(implementation = RestaurantOwnerOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "404", description = "Dono de restaurante não encontrado."),
            @ApiResponse(responseCode = "409", description = "O e-mail informado já pertence a outra pessoa.")
    })
    public ResponseEntity<RestaurantOwnerOutputDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRestaurantOwnerJson json
    ) {
        return ResponseEntity.ok(restaurantOwnerController.update(json.toInputDto(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um dono de restaurante",
            description = "Remove o papel de dono, o que não é permitido enquanto ele tiver restaurantes. A pessoa "
                    + "continua cadastrada, com os outros papéis que tiver. Para excluí-la, use DELETE /api/persons/{id}.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do dono (o mesmo da pessoa)", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dono de restaurante excluído."),
            @ApiResponse(responseCode = "404", description = "Dono de restaurante não encontrado."),
            @ApiResponse(responseCode = "409", description = "O dono ainda tem restaurantes.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restaurantOwnerController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
