package br.com.fiap.restaurante.infra.security;

import br.com.fiap.restaurante.core.controller.MenuItemController;
import br.com.fiap.restaurante.core.controller.RestaurantController;
import br.com.fiap.restaurante.core.controller.RestaurantOwnerController;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.domain.UserTypeEnum;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;
import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.dto.OpeningHoursInputDto;
import br.com.fiap.restaurante.core.dto.RequesterDto;
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RestaurantOwnershipApiTest {

    private static final String PASSWORD = "senhaForte1";
    private static final String ADMIN = "admin.posse";
    private static final String OWNER = "dono.posse";
    private static final String OTHER_OWNER = "outro.dono.posse";

    private static final AddressInputDto ADDRESS =
            new AddressInputDto("Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310100");

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

    @Autowired
    private RestaurantOwnerController restaurantOwnerController;

    @Autowired
    private RestaurantController restaurantController;

    @Autowired
    private MenuItemController menuItemController;

    /** Pessoa do usuário {@link #OWNER}. */
    private Long ownerId;

    /** Pessoa do usuário {@link #OTHER_OWNER}. */
    private Long otherOwnerId;

    /** Restaurante do {@link #OWNER}, com um prato. */
    private Long restaurantId;
    private Long menuItemId;

    private static String basic(String login) {
        String credentials = login + ":" + PASSWORD;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static String createRestaurantJson(String cnpj, Long ownerId) {
        return """
                {"name": "Cantina", "cnpj": "%s", "cuisineType": "Italiana", "ownerId": %d,
                 "address": {"street": "Av. Paulista", "number": "1000", "neighborhood": "Bela Vista",
                             "city": "São Paulo", "state": "SP", "zipCode": "01310100"},
                 "openingHours": [{"dayOfWeek": "MONDAY", "openingTime": "11:00", "closingTime": "15:00"}]}
                """.formatted(cnpj, ownerId);
    }

    private static String updateRestaurantJson(Long ownerId) {
        return """
                {"name": "Cantina Nova", "cuisineType": "Italiana", "ownerId": %d,
                 "address": {"street": "Av. Paulista", "number": "1000", "neighborhood": "Bela Vista",
                             "city": "São Paulo", "state": "SP", "zipCode": "01310100"},
                 "openingHours": [{"dayOfWeek": "MONDAY", "openingTime": "11:00", "closingTime": "15:00"}]}
                """.formatted(ownerId);
    }

    private String createMenuItemJson() {
        return """
                {"restaurantId": %d, "name": "Lasanha", "description": "Lasanha à bolonhesa", "price": 49.90}
                """.formatted(restaurantId);
    }

    private static final String UPDATE_MENU_ITEM_JSON = """
            {"name": "Pizza grande", "description": "Pizza de queijo grande", "price": 59.90, "dineInOnly": true}
            """;

    @BeforeEach
    void createOwnersAndRestaurant() {
        // nos testes o Flyway não roda, então o tipo que a migration V2 cadastra é criado aqui
        if (!userTypeRepository.existsByCode(UserType.RESTAURANT_OWNER_CODE)) {
            userTypeRepository.save(new UserTypeEntity(null, "Dono de Restaurante", UserType.RESTAURANT_OWNER_CODE));
        }
        // o cadastro do dono já cria o usuário dele, com o tipo restaurant_owner
        ownerId = registerOwner("71428793860", OWNER);
        otherOwnerId = registerOwner("87748248800", OTHER_OWNER);

        createUser(5001L, ADMIN, UserTypeEnum.ADMIN.getCode());

        RequesterDto owner = new RequesterDto(ownerId, false);
        restaurantId = restaurantController.create(new CreateRestaurantInputDto(
                "Cantina do Dono", "71428793000110", "Italiana", ownerId, ADDRESS,
                List.of(new OpeningHoursInputDto(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(15, 0)))),
                owner).id();
        menuItemId = menuItemController.create(new CreateMenuItemInputDto(
                restaurantId, "Pizza", "Pizza de queijo", new BigDecimal("39.90"), false, null), owner).id();
    }

    private Long registerOwner(String cpf, String login) {
        return restaurantOwnerController.register(new CreateRestaurantOwnerInputDto(
                "Dono " + cpf, cpf + "@email.com", cpf, null, "contato" + cpf + "@cantina.com"),
                new LoginInputDto(login, PASSWORD)).id();
    }

    private void createUser(Long personId, String login, String userTypeCode) {
        Long userTypeId = userTypeRepository.findAll().stream()
                                            .filter(type -> type.getCode().equals(userTypeCode))
                                            .findFirst()
                                            .orElseGet(() -> userTypeRepository.save(
                                                    new UserTypeEntity(null, userTypeCode, userTypeCode)))
                                            .getId();
        Long appUserId = appUserRepository.save(
                new AppUserEntity(null, personId, login, passwordEncoder.encode(PASSWORD))).getId();
        appUserTypeRepository.save(new AppUserTypeEntity(appUserId, userTypeId));
    }

    private ResultActions perform(MockHttpServletRequestBuilder request, String login) throws Exception {
        return mockMvc.perform(request.header(HttpHeaders.AUTHORIZATION, basic(login)));
    }

    private ResultActions performJson(MockHttpServletRequestBuilder request, String body, String login)
            throws Exception {
        return perform(request.contentType(MediaType.APPLICATION_JSON).content(body), login);
    }

    private static void expectForbidden(ResultActions result, String code) throws Exception {
        result.andExpect(status().isForbidden())
              .andExpect(jsonPath("$.code").value(code));
    }

    @Test
    void ownerShouldManageOwnRestaurantAndMenu() throws Exception {
        performJson(put("/api/restaurants/" + restaurantId), updateRestaurantJson(ownerId), OWNER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cantina Nova"));
        performJson(post("/api/menu-items"), createMenuItemJson(), OWNER)
                .andExpect(status().isCreated());
        performJson(put("/api/menu-items/" + menuItemId), UPDATE_MENU_ITEM_JSON, OWNER)
                .andExpect(status().isOk());
        perform(delete("/api/menu-items/" + menuItemId), OWNER)
                .andExpect(status().isNoContent());
        perform(delete("/api/restaurants/" + restaurantId), OWNER)
                .andExpect(status().isNoContent());
    }

    @Test
    void ownerShouldNotTouchRestaurantOfAnotherOwner() throws Exception {
        expectForbidden(performJson(put("/api/restaurants/" + restaurantId), updateRestaurantJson(ownerId),
                                    OTHER_OWNER), "restaurant.notOwner");
        expectForbidden(perform(delete("/api/restaurants/" + restaurantId), OTHER_OWNER), "restaurant.notOwner");
        expectForbidden(performJson(post("/api/menu-items"), createMenuItemJson(), OTHER_OWNER),
                        "restaurant.notOwner");
        expectForbidden(performJson(put("/api/menu-items/" + menuItemId), UPDATE_MENU_ITEM_JSON, OTHER_OWNER),
                        "restaurant.notOwner");
        expectForbidden(perform(delete("/api/menu-items/" + menuItemId), OTHER_OWNER), "restaurant.notOwner");

        perform(get("/api/restaurants/" + restaurantId), OTHER_OWNER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cantina do Dono"));
        perform(get("/api/menu-items/" + menuItemId), OTHER_OWNER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    void ownerShouldCreateRestaurantOnlyInOwnName() throws Exception {
        expectForbidden(performJson(post("/api/restaurants"), createRestaurantJson("87748248000120", ownerId),
                                    OTHER_OWNER), "restaurant.notOwner");

        performJson(post("/api/restaurants"), createRestaurantJson("87748248000120", otherOwnerId), OTHER_OWNER)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(otherOwnerId));
    }

    @Test
    void onlyAdminShouldTransferRestaurant() throws Exception {
        expectForbidden(performJson(put("/api/restaurants/" + restaurantId), updateRestaurantJson(otherOwnerId),
                                    OWNER), "restaurant.transferNotAllowed");

        performJson(put("/api/restaurants/" + restaurantId), updateRestaurantJson(otherOwnerId), ADMIN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(otherOwnerId));

        // Depois da transferência, o antigo dono já não pode mexer no restaurante.
        expectForbidden(perform(delete("/api/restaurants/" + restaurantId), OWNER), "restaurant.notOwner");
    }

    @Test
    void adminShouldManageAnyRestaurant() throws Exception {
        perform(delete("/api/menu-items/" + menuItemId), ADMIN).andExpect(status().isNoContent());
        perform(delete("/api/restaurants/" + restaurantId), ADMIN).andExpect(status().isNoContent());
    }
}
