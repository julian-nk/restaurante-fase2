package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.core.domain.UserTypeEnum;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserTypeRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiAuthorizationTest {

    private static final String PASSWORD = "senhaForte1";
    private static final String ADMIN = "admin.teste";
    private static final String OWNER = "dono.teste";
    private static final String CUSTOMER = "cliente.teste";

    /**
     * Prato de um restaurante que não existe: se passar da segurança, o caso de uso responde 404.
     */
    private static final String MENU_ITEM_JSON = """
            {"restaurantId": 999999, "name": "Pizza", "description": "Pizza de queijo", "price": 39.90}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private AppUserTypeRepository appUserTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long customerId;
    private Long ownerUserId;

    private static String basic(String login) {
        String credentials = login + ":" + PASSWORD;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, String body) {
        return request.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    private static void expectForbidden(ResultActions result) throws Exception {
        result.andExpect(status().isForbidden())
              .andExpect(jsonPath("$.error").value("Forbidden"))
              .andExpect(jsonPath("$.code").value("auth.forbidden"));
    }

    @BeforeEach
    void createUsers() {
        createUser(3001L, ADMIN, UserTypeEnum.ADMIN.getCode());
        ownerUserId = createUser(3002L, OWNER, UserTypeEnum.RESTAURANT_OWNER.getCode());
        customerId = createUser(3003L, CUSTOMER, UserTypeEnum.CUSTOMER.getCode());
    }

    private Long createUser(Long personId, String login, String userTypeCode) {
        Long userTypeId = userTypeRepository.findAll().stream()
                                            .filter(type -> type.getCode().equals(userTypeCode))
                                            .findFirst()
                                            .orElseGet(() -> userTypeRepository.save(
                                                    new UserTypeEntity(null, userTypeCode, userTypeCode)))
                                            .getId();
        Long appUserId = appUserRepository.save(
                new AppUserEntity(null, personId, login, passwordEncoder.encode(PASSWORD))).getId();
        appUserTypeRepository.save(new AppUserTypeEntity(appUserId, userTypeId));
        return appUserId;
    }

    private ResultActions perform(MockHttpServletRequestBuilder request, String login) throws Exception {
        return mockMvc.perform(request.header(HttpHeaders.AUTHORIZATION, basic(login)));
    }

    @Test
    void customerShouldReadRestaurantsAndMenu() throws Exception {
        perform(get("/api/restaurants"), CUSTOMER).andExpect(status().isOk());
        perform(get("/api/menu-items"), CUSTOMER).andExpect(status().isOk());
        perform(get("/api/menu-items/restaurant/1"), CUSTOMER).andExpect(status().isOk());
    }

    @Test
    void customerShouldNotCreateUpdateOrDeleteMenuItems() throws Exception {
        expectForbidden(perform(json(post("/api/menu-items"), MENU_ITEM_JSON), CUSTOMER));
        expectForbidden(perform(json(put("/api/menu-items/1"), "{}"), CUSTOMER));
        expectForbidden(perform(delete("/api/menu-items/1"), CUSTOMER));
    }

    @Test
    void restaurantOwnerAndAdminShouldManageMenuItems() throws Exception {
        perform(json(post("/api/menu-items"), MENU_ITEM_JSON), OWNER)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("restaurant.notFound"));
        perform(json(post("/api/menu-items"), MENU_ITEM_JSON), ADMIN)
                .andExpect(status().isNotFound());
        perform(delete("/api/menu-items/999999"), OWNER).andExpect(status().isNotFound());
    }

    @Test
    void customerShouldNotManageRestaurants() throws Exception {
        expectForbidden(perform(json(post("/api/restaurants"), "{}"), CUSTOMER));
        expectForbidden(perform(delete("/api/restaurants/1"), CUSTOMER));
        perform(delete("/api/restaurants/999999"), OWNER).andExpect(status().isNotFound());
    }

    @Test
    void onlyAdminShouldManageUserTypesUsersAndPeople() throws Exception {
        expectForbidden(perform(get("/api/user-types"), OWNER));
        expectForbidden(perform(get("/api/user-types"), CUSTOMER));
        expectForbidden(perform(post("/api/app-users/1/types/1"), OWNER));
        expectForbidden(perform(get("/api/persons"), OWNER));
        expectForbidden(perform(json(post("/api/restaurant-owners"), "{}"), OWNER));
        expectForbidden(perform(get("/api/customers"), CUSTOMER));

        perform(get("/api/user-types"), ADMIN).andExpect(status().isOk());
        perform(get("/api/persons"), ADMIN).andExpect(status().isOk());
    }

    @Test
    void anyUserShouldReachPasswordChange() throws Exception {
        // passa da segurança; quem barra a senha atual errada é o caso de uso
        perform(json(patch("/api/app-users/" + customerId + "/password"),
                     "{\"currentPassword\": \"senhaErrada1\", \"newPassword\": \"senhaNova123\"}"), CUSTOMER)
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.code").value("appUser.invalidPassword"));
    }

    @Test
    void onlyTheUserShouldChangeTheirOwnPassword() throws Exception {
        // todos os usuários deste teste têm a mesma senha: nem sabendo a senha atual do outro dá para trocá-la
        String body = "{\"currentPassword\": \"" + PASSWORD + "\", \"newPassword\": \"senhaNova123\"}";

        expectPasswordChangeNotAllowed(perform(json(patch("/api/app-users/" + ownerUserId + "/password"), body), CUSTOMER));
        expectPasswordChangeNotAllowed(perform(json(patch("/api/app-users/" + ownerUserId + "/password"), body), ADMIN));
        // um ID que não existe recebe a mesma resposta, para não revelar quais usuários existem
        expectPasswordChangeNotAllowed(perform(json(patch("/api/app-users/999999/password"), body), CUSTOMER));

        perform(json(patch("/api/app-users/" + ownerUserId + "/password"), body), OWNER)
                .andExpect(status().isNoContent());
    }

    private static void expectPasswordChangeNotAllowed(ResultActions result) throws Exception {
        result.andExpect(status().isForbidden())
              .andExpect(jsonPath("$.code").value("appUser.passwordChangeNotAllowed"));
    }
}
