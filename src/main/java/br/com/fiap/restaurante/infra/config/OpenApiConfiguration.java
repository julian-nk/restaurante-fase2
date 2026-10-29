package br.com.fiap.restaurante.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuração da documentação da API (springdoc/Swagger), que fica disponível em {@code /my-docs}.
 */
@Configuration
public class OpenApiConfiguration {

    private static final String BASIC_AUTH = "basicAuth";
    private static final String ERROR_SCHEMA = "Erro";

    private static final String DESCRIPTION = """
            API do Tech Challenge (fase 2) para gestão de restaurantes.
            """;

    /**
     * Descrição de cada DTO de saída, pelo nome do schema gerado pelo springdoc.
     */
    private static final Map<String, String> OUTPUT_SCHEMA_DESCRIPTIONS = Map.ofEntries(
            Map.entry("AddressOutputDto", "Endereço."),
            Map.entry("AppUserOutputDto", "Usuário de acesso ao sistema. O hash da senha nunca é devolvido."),
            Map.entry("CustomerOutputDto", "Cliente, com os dados da pessoa."),
            Map.entry("MenuItemOutputDto", "Prato do cardápio."),
            Map.entry("OpeningHoursOutputDto", "Horário de funcionamento em um dia da semana."),
            Map.entry("RestaurantOutputDto", "Restaurante, com endereço e horários de funcionamento."),
            Map.entry("RestaurantOwnerOutputDto", "Dono de restaurante, com os dados da pessoa."),
            Map.entry("UserTypeOutputDto", "Tipo de usuário.")
    );

    /**
     * Descrição dos campos dos DTOs de saída. Os nomes se repetem entre os DTOs com o mesmo sentido.
     */
    private static final Map<String, String> OUTPUT_PROPERTY_DESCRIPTIONS = Map.ofEntries(
            Map.entry("id", "Identificador gerado pelo sistema. Em clientes e donos de restaurante, é o ID da pessoa."),
            Map.entry("personId", "ID da pessoa a que o registro pertence."),
            Map.entry("name", "Nome."),
            Map.entry("email", "E-mail."),
            Map.entry("cpf", "CPF, somente dígitos."),
            Map.entry("address", "Endereço."),
            Map.entry("street", "Logradouro."),
            Map.entry("number", "Número."),
            Map.entry("complement", "Complemento."),
            Map.entry("neighborhood", "Bairro."),
            Map.entry("city", "Cidade."),
            Map.entry("state", "UF."),
            Map.entry("zipCode", "CEP."),
            Map.entry("login", "Login de acesso."),
            Map.entry("code", "Código único do tipo de usuário."),
            Map.entry("createdOn", "Data e hora do cadastro."),
            Map.entry("businessEmail", "E-mail comercial."),
            Map.entry("restaurantId", "ID do restaurante."),
            Map.entry("description", "Descrição do prato."),
            Map.entry("price", "Preço, em reais."),
            Map.entry("dineInOnly", "Se verdadeiro, o prato só é servido no restaurante (não vai para delivery)."),
            Map.entry("photoUrl", "URL da foto do prato."),
            Map.entry("cnpj", "CNPJ, somente dígitos."),
            Map.entry("cuisineType", "Tipo de cozinha."),
            Map.entry("ownerId", "ID do dono do restaurante (o ID da pessoa)."),
            Map.entry("openingHours", "Horários de funcionamento."),
            Map.entry("dayOfWeek", "Dia da semana, em inglês (MONDAY a SUNDAY)."),
            Map.entry("openingTime", "Horário de abertura."),
            Map.entry("closingTime",
                      "Horário de fechamento. Se for anterior à abertura, o restaurante fecha no dia seguinte.")
    );

    private static void describeOutputSchema(String name, Schema schema) {
        if (!name.endsWith("OutputDto")) {
            return;
        }
        if (schema.getDescription() == null) {
            schema.setDescription(OUTPUT_SCHEMA_DESCRIPTIONS.get(name));
        }
        Map<String, Schema> properties = schema.getProperties();
        if (properties == null) {
            return;
        }
        properties.forEach((property, propertySchema) -> {
            if (propertySchema.getDescription() == null) {
                propertySchema.setDescription(OUTPUT_PROPERTY_DESCRIPTIONS.get(property));
            }
        });
    }

    private static boolean isErrorWithBody(String status) {
        return (status.startsWith("4") && !status.equals("401")) || status.startsWith("5");
    }

    private static Content errorContent() {
        return new Content().addMediaType("application/json",
                                          new MediaType().schema(
                                                  new Schema<>().$ref("#/components/schemas/" + ERROR_SCHEMA)));
    }

    /**
     * Espelha o corpo montado pelo {@code GlobalExceptionHandler}.
     */
    private static Schema<?> errorSchema() {
        return new ObjectSchema()
                .description("Corpo das respostas de erro.")
                .addProperty("error", new StringSchema()
                        .description("Nome do status HTTP.")
                        .example("Not Found"))
                .addProperty("code", new StringSchema()
                        .description("Código estável do erro, para o cliente tratar cada caso. "
                                     + "Não aparece nos erros de validação dos campos.")
                        .example("userType.notFound"))
                .addProperty("message", new StringSchema()
                        .description("Explicação do erro, em português. Nos erros de validação, lista o "
                                     + "problema de cada campo.")
                        .example("Tipo de usuário não encontrado com o ID: 1"));
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                              .title("API de Gestão de Restaurantes")
                              .version("v1")
                              .description(DESCRIPTION))
                .components(new Components()
                                    .addSecuritySchemes(BASIC_AUTH, new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("basic")
                                            .description("Usuário e senha configurados no Spring Security.")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH));
    }

    /**
     * Completa as respostas de todas as operações com o que é comum a elas, para que as APIs só precisem
     * declarar os casos específicos de cada operação.
     *
     * <ul>
     *     <li>Toda operação ganha as respostas 401 (sem autenticação) e 500 (erro inesperado).</li>
     *     <li>Toda resposta de erro, exceto a 401, passa a mostrar o corpo no formato do schema {@code Erro}.
     *     Assim, nas APIs basta escrever o código e a descrição do erro.</li>
     *     <li>Se a operação declara 201 ou 204, a resposta 200 sai. O springdoc não enxerga o status definido
     *     dentro de {@code ResponseEntity.status(...)} e acrescentaria um 200 que a API nunca devolve.</li>
     * </ul>
     */
    @Bean
    public OpenApiCustomizer standardResponsesCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            openApi.getComponents().addSchemas(ERROR_SCHEMA, errorSchema());

            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().values().forEach(path -> path.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();
                if (responses == null) {
                    return;
                }
                if (responses.containsKey("201") || responses.containsKey("204")) {
                    responses.remove("200");
                }
                responses.putIfAbsent("401", new ApiResponse()
                        .description("Não autenticado: informe usuário e senha (HTTP Basic)."));
                responses.putIfAbsent("500", new ApiResponse()
                        .description("Erro inesperado, como uma falha de acesso ao banco de dados."));
                responses.forEach((status, response) -> {
                    if (isErrorWithBody(status)) {
                        response.setContent(errorContent());
                    }
                });
            }));
        };
    }

    @Bean
    public OpenApiCustomizer outputSchemaDescriptionsCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }
            openApi.getComponents().getSchemas().forEach(OpenApiConfiguration::describeOutputSchema);
        };
    }
}
