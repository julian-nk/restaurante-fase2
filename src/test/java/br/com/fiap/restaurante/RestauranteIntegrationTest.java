package br.com.fiap.restaurante;

import br.com.fiap.restaurante.core.controller.*;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.*;
import br.com.fiap.restaurante.core.exception.*;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sobe o contexto completo (H2) e percorre core controller -> use case -> gateway JPA -> banco,
 * garantindo que a injeção de dependência e os mapeamentos estão corretos.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RestauranteIntegrationTest {

    private static final AddressInputDto ADDRESS =
            new AddressInputDto("Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310-100");

    @Autowired
    private UserTypeController userTypeController;

    @Autowired
    private MenuItemController menuItemController;

    @Autowired
    private AppUserController appUserController;

    @Autowired
    private CustomerController customerController;

    @Autowired
    private RestaurantOwnerController restaurantOwnerController;

    @Autowired
    private RestaurantController restaurantController;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PersonRepository personRepository;

    private static OpeningHoursInputDto hours(DayOfWeek day, int opening, int closing) {
        return new OpeningHoursInputDto(day, LocalTime.of(opening, 0), LocalTime.of(closing, 0));
    }

    /** Cadastra o dono (com os dados da pessoa) e um restaurante dele, e devolve o restaurante. */
    private RestaurantOutputDto createRestaurant(String cpf, String cnpj) {
        RestaurantOwnerOutputDto owner = restaurantOwnerController.register(new CreateRestaurantOwnerInputDto(
                "Dono " + cpf, cpf + "@email.com", cpf, null, "contato" + cpf + "@cantina.com"));

        return restaurantController.create(new CreateRestaurantInputDto("Cantina", cnpj, "Italiana", owner.id(),
                ADDRESS, List.of(hours(DayOfWeek.MONDAY, 11, 15), hours(DayOfWeek.FRIDAY, 18, 2))));
    }

    @Test
    void shouldManageUserTypeThroughAllLayers() {
        UserTypeOutputDto created = userTypeController.create(
                new CreateUserTypeInputDto("Cliente de integração", "integration_customer"));

        assertNotNull(created.id());
        assertTrue(userTypeRepository.existsByCode("integration_customer"));
        assertEquals(created, userTypeController.findById(created.id()));

        UserTypeOutputDto updated = userTypeController.update(
                new UpdateUserTypeInputDto(created.id(), "Cliente VIP", "integration_vip"));
        assertEquals("integration_vip", updated.code());

        userTypeController.delete(created.id());
        Long id = created.id();
        assertThrows(UserTypeNotFoundException.class, () -> userTypeController.findById(id));
    }

    @Test
    void shouldRejectDuplicatedUserTypeCode() {
        userTypeController.create(new CreateUserTypeInputDto("Admin", "integration_admin"));

        assertThrows(UserTypeAlreadyExistsException.class,
                () -> userTypeController.create(new CreateUserTypeInputDto("Outro admin", "integration_admin")));
    }

    @Test
    void shouldManageMenuItemThroughAllLayers() {
        Long restaurantId = createRestaurant("11111111111", "11111111000111").id();

        MenuItemOutputDto created = menuItemController.create(new CreateMenuItemInputDto(
                restaurantId, "Pizza", "Pizza de queijo", new BigDecimal("39.90"), false, "/fotos/pizza.png"));

        assertNotNull(created.id());
        assertEquals(1, menuItemController.findByRestaurantId(restaurantId).size());

        MenuItemOutputDto updated = menuItemController.update(new UpdateMenuItemInputDto(
                created.id(), "Pizza grande", "Pizza de queijo grande", new BigDecimal("59.90"), true, null));
        assertEquals("Pizza grande", updated.name());
        assertEquals(0, new BigDecimal("59.90").compareTo(menuItemController.findById(created.id()).price()));

        menuItemController.delete(created.id());
        Long id = created.id();
        assertThrows(MenuItemNotFoundException.class, () -> menuItemController.findById(id));
    }

    @Test
    void shouldNotCreateMenuItemForUnknownRestaurant() {
        assertThrows(RestaurantNotFoundException.class, () -> menuItemController.create(new CreateMenuItemInputDto(
                999_999L, "Pizza", "Pizza de queijo", new BigDecimal("39.90"), false, null)));
    }

    @Test
    void shouldManageBothRolesOfTheSamePerson() {
        CustomerOutputDto customer = customerController.register(
                new CreateCustomerInputDto("Maria", "maria@email.com", "12345678901", ADDRESS));
        Long personId = customer.id();
        Long addressId = customer.address().id();
        assertTrue(addressRepository.existsById(addressId));
        assertThrows(CustomerAlreadyRegisteredException.class, () -> customerController.register(
                new CreateCustomerInputDto("Maria", "maria@email.com", "12345678901", ADDRESS)));

        // mesmo CPF, agora com pontuação, a pessoa é reaproveitada e os dados enviados substituem os gravados
        RestaurantOwnerOutputDto owner = restaurantOwnerController.register(new CreateRestaurantOwnerInputDto(
                "Maria Souza", "maria.souza@email.com", "123.456.789-01", null, "contato@cantinadamaria.com"));
        assertEquals("12345678901", owner.cpf());
        assertEquals(personId, owner.id());
        assertEquals("Maria Souza", customerController.findById(personId).name());
        assertNull(customerController.findById(personId).address());
        assertFalse(addressRepository.existsById(addressId));

        assertThrows(EmailAlreadyRegisteredException.class, () -> customerController.register(
                new CreateCustomerInputDto("Outra", "maria.souza@email.com", "98765432100", null)));

        CustomerOutputDto updated = customerController.update(
                new UpdateCustomerInputDto(personId, "Maria Souza", "maria.souza@email.com", ADDRESS));
        assertEquals("Av. Paulista", updated.address().street());
        assertEquals("Av. Paulista", restaurantOwnerController.findById(personId).address().street());

        AppUserOutputDto user = appUserController.create(new CreateAppUserInputDto(personId, "maria", "senhaForte1"));
        appUserController.changePassword(new ChangePasswordInputDto(user.id(), "senhaForte1", "senhaNova2"));
        assertThrows(InvalidPasswordException.class, () -> appUserController.changePassword(
                new ChangePasswordInputDto(user.id(), "senhaForte1", "outraSenha3")));
        assertThrows(PersonAlreadyHasUserException.class, () -> appUserController.create(
                new CreateAppUserInputDto(personId, "maria2", "senhaForte1")));

        // ainda é dona, deixar de ser cliente, mas não apaga a pessoa
        customerController.delete(personId);
        assertThrows(CustomerNotFoundException.class, () -> customerController.findById(personId));
        assertTrue(personRepository.existsById(personId));

        // era o último papel, deleta junto
        restaurantOwnerController.delete(personId);
        assertFalse(personRepository.existsById(personId));
    }

    @Test
    void shouldManageRestaurantThroughAllLayers() {
        RestaurantOutputDto created = createRestaurant("22222222222", "22.222.222/0001-22");
        Long ownerId = created.ownerId();

        assertEquals("22222222000122", created.cnpj());
        assertEquals(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                created.openingHours().stream().map(OpeningHoursOutputDto::dayOfWeek).toList());

        RestaurantOutputDto updated = restaurantController.update(new UpdateRestaurantInputDto(created.id(),
                "Cantina Nova", "Italiana", ownerId,
                new AddressInputDto("Rua Augusta", "500", null, "Consolação", "São Paulo", StatesEnum.SP, "01305000"),
                List.of(hours(DayOfWeek.MONDAY, 11, 15), hours(DayOfWeek.TUESDAY, 11, 15))));

        assertEquals("Cantina Nova", updated.name());
        assertEquals(created.address().id(), updated.address().id());
        assertEquals("Rua Augusta", restaurantController.findById(created.id()).address().street());
        assertEquals(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                restaurantController.findById(created.id()).openingHours().stream()
                        .map(OpeningHoursOutputDto::dayOfWeek).toList());
        assertEquals(1, restaurantController.findAll().size());

        assertThrows(CnpjAlreadyRegisteredException.class, () -> restaurantController.create(new CreateRestaurantInputDto(
                "Outra", "22222222000122", "Japonesa", ownerId, ADDRESS, List.of(hours(DayOfWeek.MONDAY, 11, 15)))));
        assertThrows(RestaurantOwnerHasRestaurantsException.class, () -> restaurantOwnerController.delete(ownerId));

        restaurantController.delete(created.id());
        assertThrows(RestaurantNotFoundException.class, () -> restaurantController.findById(created.id()));
        assertFalse(addressRepository.existsById(created.address().id()));

        restaurantOwnerController.delete(ownerId);
        assertThrows(RestaurantOwnerNotFoundException.class, () -> restaurantOwnerController.findById(ownerId));
    }
}
