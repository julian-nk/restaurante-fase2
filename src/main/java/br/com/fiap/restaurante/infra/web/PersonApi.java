package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.PersonController;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.infra.web.json.CreatePersonJson;
import br.com.fiap.restaurante.infra.web.json.UpdatePersonJson;
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

@Tag(name = "Pessoas",
        description = "Todas as pessoas cadastradas, tenham elas papel ou não. Uma pessoa sem papel pode ser, por "
                + "exemplo, um administrador; cliente e dono de restaurante são papéis da mesma pessoa, com o mesmo ID.")
@RestController
@RequestMapping("/api/persons")
public class PersonApi {

    private final PersonController personController;

    public PersonApi(PersonController personController) {
        this.personController = personController;
    }

    @PostMapping
    @Operation(summary = "Cadastra uma pessoa",
            description = "Cadastra a pessoa sem nenhum papel. Para que ela passe a ser cliente ou dona de "
                    + "restaurante, use o cadastro do papel com o mesmo CPF.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pessoa cadastrada.",
                    content = @Content(schema = @Schema(implementation = PersonOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "409", description = "O CPF ou o e-mail já pertence a outra pessoa.")
    })
    public ResponseEntity<PersonOutputDto> create(@Valid @RequestBody CreatePersonJson json) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personController.create(json.toInputDto()));
    }

    @GetMapping
    @Operation(summary = "Lista as pessoas", description = "Inclui as pessoas sem papel, os clientes e os donos de restaurante.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoas cadastradas.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PersonOutputDto.class))))
    })
    public ResponseEntity<List<PersonOutputDto>> findAll() {
        return ResponseEntity.ok(personController.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pessoa pelo ID")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID da pessoa", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoa encontrada.",
                    content = @Content(schema = @Schema(implementation = PersonOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada.")
    })
    public ResponseEntity<PersonOutputDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(personController.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma pessoa",
            description = "Substitui nome, e-mail e endereço, inclusive de quem é cliente ou dono de restaurante. "
                    + "O CPF não pode ser alterado; se o endereço for omitido, o endereço atual é removido.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID da pessoa", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pessoa atualizada.",
                    content = @Content(schema = @Schema(implementation = PersonOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada."),
            @ApiResponse(responseCode = "409", description = "O e-mail informado já pertence a outra pessoa.")
    })
    public ResponseEntity<PersonOutputDto> update(@PathVariable Long id, @Valid @RequestBody UpdatePersonJson json) {
        return ResponseEntity.ok(personController.update(json.toInputDto(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma pessoa",
            description = "Exclui a pessoa com o endereço, os papéis (cliente e dono de restaurante) e o usuário de "
                    + "acesso. Não é permitido enquanto ela for dona de restaurantes.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID da pessoa", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pessoa excluída."),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada."),
            @ApiResponse(responseCode = "409", description = "A pessoa ainda é dona de restaurantes.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
