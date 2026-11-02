package br.com.fiap.restaurante.infra.config;

import br.com.fiap.restaurante.infra.security.ApiAccessRules;
import br.com.fiap.restaurante.infra.security.Rule;
import br.com.fiap.restaurante.infra.security.UserTypeEnum;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Configuração da documentação da API (springdoc/Swagger).
 */
@Configuration
public class OpenApiConfiguration {

    private static final String BASIC_AUTH = "basicAuth";
    private static final String ERROR_SCHEMA = "Erro";
    private static final String ACCESS_LABEL = "Acesso: ";

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
            Map.entry("PersonOutputDto", "Pessoa cadastrada, tenha ela papel (cliente ou dono de restaurante) ou não."),
            Map.entry("RestaurantOutputDto", "Restaurante, com endereço e horários de funcionamento."),
            Map.entry("RestaurantOwnerOutputDto", "Dono de restaurante, com os dados da pessoa e os restaurantes dele."),
            Map.entry("RestaurantSummaryOutputDto", "Resumo de um restaurante. Os dados completos, com endereço e "
                                                    + "horários, ficam em /api/restaurants/{id}."),
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
            Map.entry("restaurants", "Restaurantes de que a pessoa é dona, em ordem de ID."),
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
        return status.startsWith("4") || status.startsWith("5");
    }

    private static void describeAccess(String path, String method, Operation operation) {
        Optional<Rule> rule = ApiAccessRules.ruleFor(HttpMethod.valueOf(method), path);
        if (rule.isEmpty()) {
            return;
        }
        ApiResponses responses = operation.getResponses();
        responses.putIfAbsent("401", new ApiResponse()
                .description("Não autenticado: informe o login e a senha de um usuário (HTTP Basic)."));

        String access;
        if (rule.get().allowsAnyAuthenticatedUser()) {
            access = "qualquer usuário autenticado.";
        } else {
            String tipos = rule.get().userTypeCodes().stream()
                               .map(UserTypeEnum::getCode)
                               .collect(Collectors.joining(" ou "));
            access = "usuários do tipo " + tipos + ".";
            responses.putIfAbsent("403", new ApiResponse()
                    .description("Sem permissão: o tipo do usuário não pode chamar esta operação."));
        }

        String description = operation.getDescription();
        if (description == null || description.isBlank()) {
            operation.setDescription(ACCESS_LABEL + access);
        } else if (!description.contains(ACCESS_LABEL)) {
            operation.setDescription(description + "\n\n" + ACCESS_LABEL + access);
        }
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
                                            .description("Login e senha de um usuário cadastrado na aplicação.")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH));
    }

    /**
     * Completa as respostas de todas as operações com o que é comum a elas, para que as APIs só precisem
     * declarar os casos específicos de cada operação.
     *
     * <ul>
     *     <li>Toda operação ganha as respostas 401 (sem autenticação) e 500 (erro inesperado), e a 403 quando só
     *     alguns tipos de usuário podem chamá-la. A descrição da operação diz quem tem acesso.</li>
     *     <li>Toda resposta de erro passa a mostrar o corpo no formato do schema {@code Erro}. Assim, nas APIs
     *     basta escrever o código e a descrição do erro.</li>
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
            openApi.getPaths().forEach((pathName, path) -> path.readOperationsMap().forEach((method, operation) -> {
                ApiResponses responses = operation.getResponses();
                if (responses == null) {
                    return;
                }
                if (responses.containsKey("201") || responses.containsKey("204")) {
                    responses.remove("200");
                }
                describeAccess(pathName, method.name(), operation);
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
