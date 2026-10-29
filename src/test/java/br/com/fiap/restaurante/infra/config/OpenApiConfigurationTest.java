package br.com.fiap.restaurante.infra.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Gera a documentação de verdade ({@code GET /api-docs}) e confere o que depende do {@link OpenApiConfiguration}.
 * Se um dos customizadores parar de funcionar, por exemplo numa atualização do springdoc, é aqui que aparece.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiConfigurationTest {

    private static final String CREATE_USER_TYPE = "$.paths['/api/user-types'].post";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDocumentTheApiInPortuguese() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API de Gestão de Restaurantes"))
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"))
                .andExpect(jsonPath(CREATE_USER_TYPE + ".summary").value("Cadastra um tipo de usuário"));
    }

    @Test
    void shouldKeepOnlyTheResponsesTheApiReallyReturns() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(jsonPath(CREATE_USER_TYPE + ".responses['201']").exists())
                .andExpect(jsonPath(CREATE_USER_TYPE + ".responses['200']").doesNotExist())
                .andExpect(jsonPath(CREATE_USER_TYPE + ".responses['401']").exists())
                .andExpect(jsonPath(CREATE_USER_TYPE + ".responses['500']").exists());
    }

    @Test
    void shouldDescribeErrorBodiesWithTheErrorSchema() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(jsonPath("$.components.schemas.Erro.properties.code").exists())
                .andExpect(jsonPath(CREATE_USER_TYPE + ".responses['409'].content['application/json'].schema['$ref']")
                        .value("#/components/schemas/Erro"));
    }

    @Test
    void shouldDescribeTheOutputDtosOfTheCore() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(jsonPath("$.components.schemas.UserTypeOutputDto.description").value("Tipo de usuário."))
                .andExpect(jsonPath("$.components.schemas.UserTypeOutputDto.properties.code.description")
                        .value("Código único do tipo de usuário."))
                .andExpect(jsonPath("$.components.schemas.PersonOutputDto.description")
                        .value("Pessoa cadastrada, tenha ela papel (cliente ou dono de restaurante) ou não."));
    }

    @Test
    void shouldDocumentThePersonsApi() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(jsonPath("$.paths['/api/persons'].post.summary").value("Cadastra uma pessoa"))
                .andExpect(jsonPath("$.paths['/api/persons'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/persons/{id}'].delete.responses['409']").exists());
    }
}
