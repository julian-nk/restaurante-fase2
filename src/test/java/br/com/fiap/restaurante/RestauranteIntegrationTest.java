package br.com.fiap.restaurante;

import br.com.fiap.restaurante.core.controller.*;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.dto.*;
import br.com.fiap.restaurante.core.exception.*;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RestauranteIntegrationTest {

    private static final AddressInputDto ADDRESS =
            new AddressInputDto("Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310-100");

    private static final RequesterDto ADMIN = new RequesterDto(1L, true);

    @Autowired
    private UserTypeController userTypeController;

    @Autowired
    private MenuItemController menuItemController;

    @Autowired
    private AppUserController appUserController;

    @Autowired
    private AppUserTypeController appUserTypeController;

    @Autowired
    private PersonController personController;

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

    /** Login e senha do usuário de acesso que nasce junto com a pessoa. */
    private static LoginInputDto login(String login) {
        return new LoginInputDto(login, "senhaForte1");
    }

    /** Nos testes o Flyway não roda, então os tipos dos papéis, que a migration V2 cadastra, são criados aqui. */
    @BeforeEach
    void createRoleUserTypes() {
        for (String code : List.of(UserType.CUSTOMER_CODE, UserType.RESTAURANT_OWNER_CODE)) {
            if (!userTypeRepository.existsByCode(code)) {
                userTypeController.create(new CreateUserTypeInputDto(code, code));
            }
        }
    }

    private AppUserOutputDto userOf(Long personId) {
        return appUserController.findAll().stream()
                                .filter(user -> user.personId().equals(personId))
                                .findFirst()
                                .orElseThrow();
    }

    private List<String> userTypeCodesOf(Long personId) {
        return appUserTypeController.findUserTypes(userOf(personId).id()).stream()
                                    .map(UserTypeOutputDto::code)
                                    .toList();
    }

    /** O próprio dono do restaurante, chamando os casos de uso. */
    private static RequesterDto ownerOf(RestaurantOutputDto restaurant) {
        return new RequesterDto(restaurant.ownerId(), false);
    }

    /** Cadastra o dono (com os dados da pessoa) e um restaurante dele, feito pelo próprio dono. */
    private RestaurantOutputDto createRestaurant(String cpf, String cnpj) {
        RestaurantOwnerOutputDto owner = restaurantOwnerController.register(new CreateRestaurantOwnerInputDto(
                "Dono " + cpf, cpf + "@email.com", cpf, null, "contato" + cpf + "@cantina.com"), login("dono" + cpf));

        return restaurantController.create(new CreateRestaurantInputDto("Cantina", cnpj, "Italiana", owner.id(),
                ADDRESS, List.of(hours(DayOfWeek.MONDAY, 11, 15), hours(DayOfWeek.FRIDAY, 18, 2))),
                new RequesterDto(owner.id(), false));
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
        RestaurantOutputDto restaurant = createRestaurant("11111111111", "11111111000111");
        Long restaurantId = restaurant.id();
        RequesterDto owner = ownerOf(restaurant);

        MenuItemOutputDto created = menuItemController.create(new CreateMenuItemInputDto(
                restaurantId, "Pizza", "Pizza de queijo", new BigDecimal("39.90"), false, "/fotos/pizza.png"), owner);

        assertNotNull(created.id());
        assertEquals(1, menuItemController.findByRestaurantId(restaurantId).size());

        MenuItemOutputDto updated = menuItemController.update(new UpdateMenuItemInputDto(
                created.id(), "Pizza grande", "Pizza de queijo grande", new BigDecimal("59.90"), true, null), owner);
        assertEquals("Pizza grande", updated.name());
        assertEquals(0, new BigDecimal("59.90").compareTo(menuItemController.findById(created.id()).price()));

        menuItemController.delete(created.id(), owner);
        Long id = created.id();
        assertThrows(MenuItemNotFoundException.class, () -> menuItemController.findById(id));
    }

    @Test
    void shouldNotCreateMenuItemForUnknownRestaurant() {
        assertThrows(RestaurantNotFoundException.class, () -> menuItemController.create(new CreateMenuItemInputDto(
                999_999L, "Pizza", "Pizza de queijo", new BigDecimal("39.90"), false, null), ADMIN));
    }

    @Test
    void shouldManageBothRolesOfTheSamePerson() {
        CustomerOutputDto customer = customerController.register(
                new CreateCustomerInputDto("Maria", "maria@email.com", "12345678901", ADDRESS), login("maria"));
        Long personId = customer.id();
        Long addressId = customer.address().id();
        assertTrue(addressRepository.existsById(addressId));
        assertEquals(List.of("customer"), userTypeCodesOf(personId));
        assertThrows(CustomerAlreadyRegisteredException.class, () -> customerController.register(
                new CreateCustomerInputDto("Maria", "maria@email.com", "12345678901", ADDRESS), null));

        // a pessoa já tem usuário, então o cadastro do outro papel não pode trazer outro login
        CreateRestaurantOwnerInputDto ownerInput = new CreateRestaurantOwnerInputDto(
                "Maria Souza", "maria.souza@email.com", "123.456.789-01", null, "contato@cantinadamaria.com");
        assertThrows(PersonAlreadyHasUserException.class,
                () -> restaurantOwnerController.register(ownerInput, login("maria2")));

        // mesmo CPF, agora com pontuação, a pessoa é reaproveitada e os dados enviados substituem os gravados
        RestaurantOwnerOutputDto owner = restaurantOwnerController.register(ownerInput, null);
        assertEquals(List.of("customer", "restaurant_owner"), userTypeCodesOf(personId));
        assertEquals("12345678901", owner.cpf());
        assertEquals(personId, owner.id());
        assertEquals("Maria Souza", customerController.findById(personId).name());
        assertNull(customerController.findById(personId).address());
        assertFalse(addressRepository.existsById(addressId));

        assertThrows(EmailAlreadyRegisteredException.class, () -> customerController.register(
                new CreateCustomerInputDto("Outra", "maria.souza@email.com", "98765432100", null), login("outra")));

        CustomerOutputDto updated = customerController.update(
                new UpdateCustomerInputDto(personId, "Maria Souza", "maria.souza@email.com", ADDRESS));
        assertEquals("Av. Paulista", updated.address().street());
        assertEquals("Av. Paulista", restaurantOwnerController.findById(personId).address().street());

        AppUserOutputDto user = userOf(personId);
        assertEquals("maria", user.login());
        RequesterDto maria = new RequesterDto(personId, false);
        appUserController.changePassword(new ChangePasswordInputDto(user.id(), "senhaForte1", "senhaNova2"), maria);
        assertThrows(InvalidPasswordException.class, () -> appUserController.changePassword(
                new ChangePasswordInputDto(user.id(), "senhaForte1", "outraSenha3"), maria));

        // remover um papel não apaga a pessoa, nem quando era o último, mas o usuário perde o tipo do papel
        customerController.delete(personId);
        assertThrows(CustomerNotFoundException.class, () -> customerController.findById(personId));
        assertEquals(List.of("restaurant_owner"), userTypeCodesOf(personId));
        restaurantOwnerController.delete(personId);
        assertThrows(RestaurantOwnerNotFoundException.class, () -> restaurantOwnerController.findById(personId));
        assertTrue(userTypeCodesOf(personId).isEmpty());
        assertEquals("Maria Souza", personController.findById(personId).name());
        assertEquals("Av. Paulista", personController.findById(personId).address().street());
    }

    @Test
    void shouldManagePersonWithoutRoleThroughAllLayers() {
        PersonOutputDto created = personController.create(
                new CreatePersonInputDto("Ana", "ana@email.com", "390.533.447-05", ADDRESS), login("ana"));
        Long personId = created.id();
        Long addressId = created.address().id();
        assertEquals("39053344705", created.cpf());
        assertTrue(personController.findAll().stream().anyMatch(person -> person.id().equals(personId)));
        assertEquals("ana", userOf(personId).login());
        assertTrue(userTypeCodesOf(personId).isEmpty());
        assertThrows(CpfAlreadyRegisteredException.class, () -> personController.create(
                new CreatePersonInputDto("Outra", "outra@email.com", "39053344705", null), login("outra")));
        assertThrows(EmailAlreadyRegisteredException.class, () -> personController.create(
                new CreatePersonInputDto("Outra", "ANA@email.com", "11144477735", null), login("outra")));
        assertThrows(LoginAlreadyInUseException.class, () -> personController.create(
                new CreatePersonInputDto("Outra", "outra@email.com", "11144477735", null), login("ANA")));
        assertThrows(InvalidFieldException.class, () -> personController.create(
                new CreatePersonInputDto("Outra", "outra@email.com", "11144477735", null), null));
        assertFalse(personController.findAll().stream().anyMatch(person -> person.cpf().equals("11144477735")));

        PersonOutputDto updated = personController.update(
                new UpdatePersonInputDto(personId, "Ana Pereira", "ana.pereira@email.com", null));
        assertEquals("Ana Pereira", updated.name());
        assertNull(updated.address());
        assertFalse(addressRepository.existsById(addressId));

        // a pessoa já existe, então o cadastro de cliente com o mesmo CPF só dá o papel a ela
        CustomerOutputDto customer = customerController.register(
                new CreateCustomerInputDto("Ana Pereira", "ana.pereira@email.com", "39053344705", null), null);
        assertEquals(personId, customer.id());
        assertEquals(List.of("customer"), userTypeCodesOf(personId));

        customerController.delete(personId);
        assertTrue(userTypeCodesOf(personId).isEmpty());
        assertTrue(personRepository.existsById(personId));

        personController.delete(personId);
        assertThrows(PersonNotFoundException.class, () -> personController.findById(personId));
        assertThrows(PersonNotFoundException.class, () -> personController.delete(personId));
    }

    @Test
    void shouldNotDeletePersonThatStillOwnsRestaurants() {
        Long ownerId = createRestaurant("33333333333", "33333333000133").ownerId();

        assertThrows(RestaurantOwnerHasRestaurantsException.class, () -> personController.delete(ownerId));
        assertTrue(personRepository.existsById(ownerId));
    }

    @Test
    void shouldManageTheTypesOfAnAppUserThroughAllLayers() {
        Long personId = personController.create(
                new CreatePersonInputDto("Admin", "admin@email.com", "12345678909", null), login("admin")).id();
        Long userId = userOf(personId).id();
        Long admin = userTypeController.create(new CreateUserTypeInputDto("Administrador", "integration_admin_type")).id();
        Long staff = userTypeController.create(new CreateUserTypeInputDto("Funcionário", "integration_staff")).id();

        appUserTypeController.associate(userId, admin);
        appUserTypeController.associate(userId, admin); // de novo: não duplica nem falha
        appUserTypeController.associate(userId, staff);
        assertEquals(List.of(admin, staff),
                appUserTypeController.findUserTypes(userId).stream().map(UserTypeOutputDto::id).toList());

        // trocar a senha grava o usuário de novo, e os tipos precisam continuar lá
        appUserController.changePassword(new ChangePasswordInputDto(userId, "senhaForte1", "senhaNova2"),
                                         new RequesterDto(personId, false));
        assertEquals(2, appUserTypeController.findUserTypes(userId).size());

        appUserTypeController.remove(userId, staff);
        assertEquals(List.of("integration_admin_type"),
                appUserTypeController.findUserTypes(userId).stream().map(UserTypeOutputDto::code).toList());
        assertThrows(UserTypeNotFoundException.class, () -> appUserTypeController.associate(userId, 999_999L));
    }

    @Test
    void shouldManageRestaurantThroughAllLayers() {
        RestaurantOutputDto created = createRestaurant("22222222222", "22.222.222/0001-22");
        Long ownerId = created.ownerId();
        RequesterDto owner = ownerOf(created);

        assertEquals("22222222000122", created.cnpj());
        assertEquals(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                created.openingHours().stream().map(OpeningHoursOutputDto::dayOfWeek).toList());

        RestaurantOutputDto updated = restaurantController.update(new UpdateRestaurantInputDto(created.id(),
                "Cantina Nova", "Italiana", ownerId,
                new AddressInputDto("Rua Augusta", "500", null, "Consolação", "São Paulo", StatesEnum.SP, "01305000"),
                List.of(hours(DayOfWeek.MONDAY, 11, 15), hours(DayOfWeek.TUESDAY, 11, 15))), owner);

        assertEquals("Cantina Nova", updated.name());
        assertEquals(created.address().id(), updated.address().id());
        assertEquals("Rua Augusta", restaurantController.findById(created.id()).address().street());
        assertEquals(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                restaurantController.findById(created.id()).openingHours().stream()
                        .map(OpeningHoursOutputDto::dayOfWeek).toList());
        assertEquals(1, restaurantController.findAll().size());
        assertEquals(List.of(new RestaurantSummaryOutputDto(created.id(), "Cantina Nova", "22222222000122", "Italiana")),
                     restaurantOwnerController.findById(ownerId).restaurants());

        assertThrows(CnpjAlreadyRegisteredException.class, () -> restaurantController.create(new CreateRestaurantInputDto(
                "Outra", "22222222000122", "Japonesa", ownerId, ADDRESS, List.of(hours(DayOfWeek.MONDAY, 11, 15))),
                owner));
        assertThrows(RestaurantOwnerHasRestaurantsException.class, () -> restaurantOwnerController.delete(ownerId));

        restaurantController.delete(created.id(), owner);
        assertThrows(RestaurantNotFoundException.class, () -> restaurantController.findById(created.id()));
        assertFalse(addressRepository.existsById(created.address().id()));
        assertTrue(restaurantOwnerController.findById(ownerId).restaurants().isEmpty());

        restaurantOwnerController.delete(ownerId);
        assertThrows(RestaurantOwnerNotFoundException.class, () -> restaurantOwnerController.findById(ownerId));
    }
}
