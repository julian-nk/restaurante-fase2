package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.controller.CustomerController;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.infra.web.json.RegisterCustomerJson;
import br.com.fiap.restaurante.infra.web.json.UpdateCustomerJson;
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

@Tag(name = "Clientes",
        description = "Pessoas cadastradas como clientes. O cliente é a própria pessoa: o ID dele é o da pessoa.")
@RestController
@RequestMapping("/api/customers")
public class CustomerApi {

    private final CustomerController customerController;

    public CustomerApi(CustomerController customerController) {
        this.customerController = customerController;
    }

    @PostMapping
    @Operation(summary = "Cadastra um cliente",
            description = "Cadastra a pessoa junto com o papel de cliente. Se já existir uma pessoa com o CPF "
                    + "informado (por ser dona de restaurante), ela passa a ser também cliente, e o nome, o "
                    + "e-mail e o endereço dela são substituídos pelos enviados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado.",
                    content = @Content(schema = @Schema(implementation = CustomerOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "409", description = "A pessoa já é cliente ou o e-mail pertence a outra pessoa.")
    })
    public ResponseEntity<CustomerOutputDto> register(@Valid @RequestBody RegisterCustomerJson json) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerController.register(json.toInputDto()));
    }

    @GetMapping
    @Operation(summary = "Lista os clientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes cadastrados.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerOutputDto.class))))
    })
    public ResponseEntity<List<CustomerOutputDto>> findAll() {
        return ResponseEntity.ok(customerController.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente pelo ID")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do cliente (o mesmo da pessoa)", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado.",
                    content = @Content(schema = @Schema(implementation = CustomerOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado.")
    })
    public ResponseEntity<CustomerOutputDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerController.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente",
            description = "Substitui nome, e-mail e endereço. O CPF não pode ser alterado; se o endereço for "
                    + "omitido, o endereço atual é removido.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do cliente (o mesmo da pessoa)", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado.",
                    content = @Content(schema = @Schema(implementation = CustomerOutputDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos: algum campo obrigatório faltando ou fora do formato."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado."),
            @ApiResponse(responseCode = "409", description = "O e-mail informado já pertence a outra pessoa.")
    })
    public ResponseEntity<CustomerOutputDto> update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerJson json) {
        return ResponseEntity.ok(customerController.update(json.toInputDto(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um cliente",
            description = "Remove o papel de cliente. Se a pessoa não for também dona de restaurante, ela é "
                    + "excluída junto, com o endereço e o usuário de acesso.")
    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID do cliente (o mesmo da pessoa)", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente excluído."),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
