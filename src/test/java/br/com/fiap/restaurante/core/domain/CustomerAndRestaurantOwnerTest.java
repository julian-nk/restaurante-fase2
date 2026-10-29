package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.CustomerFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.RestaurantOwnerFactoryImpl;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerAndRestaurantOwnerTest {

    private static final OffsetDateTime CREATED_ON = OffsetDateTime.parse("2026-01-10T10:00:00-03:00");
    private static final AddressInputDto ADDRESS = new AddressInputDto(
            "Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310-100");

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final CustomerFactoryImpl customerFactory = new CustomerFactoryImpl(addressFactory);
    private final RestaurantOwnerFactoryImpl ownerFactory = new RestaurantOwnerFactoryImpl(addressFactory);

    @Test
    void shouldCreateCustomerForNewPerson() {
        Customer customer = customerFactory.create(null,
                new CreateCustomerInputDto("Maria", "maria@email.com", "52998224725", ADDRESS));

        assertNull(customer.getId());
        assertEquals("Maria", customer.getName());
        assertEquals("52998224725", customer.getCpf());
        assertEquals("Bela Vista", customer.getAddress().getNeighborhood());
        assertNotNull(customer.getCreatedOn());
    }

    @Test
    void shouldKeepOnlyTheDigitsOfTheCpf() {
        Customer customer = customerFactory.create(null,
                new CreateCustomerInputDto("Maria", "maria@email.com", "529.982.247-25", null));

        assertEquals("52998224725", customer.getCpf());
        assertEquals("11144477735", Person.normalizeCpf(" 111.444.777-35 "));
    }

    @Test
    void shouldRejectCpfThatIsEmptyOrDoesNotHaveElevenDigits() {
        assertThrows(InvalidFieldException.class, () -> Person.normalizeCpf(null));
        assertThrows(InvalidFieldException.class, () -> Person.normalizeCpf("  "));
        assertThrows(InvalidFieldException.class, () -> Person.normalizeCpf("529.982.247"));
        assertThrows(InvalidFieldException.class, () -> Person.normalizeCpf("529.982.247-2X"));
        assertThrows(InvalidFieldException.class, () -> customerFactory.create(null,
                new CreateCustomerInputDto("Maria", "maria@email.com", "123", null)));
    }

    @Test
    void shouldStoreEmailsInLowercaseAndRejectInvalidPersonalData() {
        RestaurantOwner owner = ownerFactory.create(null, new CreateRestaurantOwnerInputDto(
                "João", "Joao@Email.com", "11144477735", null, "Contato@Cantina.com"));

        assertEquals("joao@email.com", owner.getEmail());
        assertEquals("contato@cantina.com", owner.getBusinessEmail());
        assertThrows(InvalidFieldException.class, () -> owner.updateBusinessEmail("sem-arroba"));
        assertThrows(InvalidFieldException.class, () -> customerFactory.create(null,
                new CreateCustomerInputDto("Maria", "maria-sem-arroba", "52998224725", null)));
        assertThrows(InvalidFieldException.class, () -> customerFactory.create(null,
                new CreateCustomerInputDto(" ", "maria@email.com", "52998224725", null)));
    }

    @Test
    void shouldCreateCustomerForPersonThatAlreadyExists() {
        Customer customer = customerFactory.create(7L,
                new CreateCustomerInputDto("Maria", "maria@email.com", "52998224725", null));

        assertEquals(7L, customer.getId());
        assertNull(customer.getAddress());
    }

    @Test
    void shouldCreateAndRestoreRestaurantOwner() {
        RestaurantOwner created = ownerFactory.create(null, new CreateRestaurantOwnerInputDto(
                "João", "joao@email.com", "11144477735", null, "contato@cantina.com"));
        RestaurantOwner restored = ownerFactory.restore(
                3L, "João", "joao@email.com", "11144477735", null, CREATED_ON, "contato@cantina.com");

        assertEquals("contato@cantina.com", created.getBusinessEmail());
        assertTrue(created.getOwnedRestaurants().isEmpty());
        assertEquals(3L, restored.getId());
        assertEquals(CREATED_ON, restored.getCreatedOn());
    }

    @Test
    void shouldReplacePersonalDataKeepingTheAddressRecord() {
        Address current = addressFactory.restore(9L, "Rua A", "1", null, "Centro", "Campinas", "SP", "13010000");
        Customer customer = customerFactory.restore(1L, "Maria", "maria@email.com", "52998224725", current, CREATED_ON);

        customer.updatePersonalData("Maria Souza", "maria.souza@email.com", addressFactory.create(ADDRESS));

        assertEquals("Maria Souza", customer.getName());
        assertEquals("maria.souza@email.com", customer.getEmail());
        assertEquals(9L, customer.getAddress().getId());
        assertEquals("Av. Paulista", customer.getAddress().getStreet());
    }

    @Test
    void shouldRemoveAddressWhenNoneIsGiven() {
        Address current = addressFactory.restore(9L, "Rua A", "1", null, "Centro", "Campinas", "SP", "13010000");
        Customer customer = customerFactory.restore(1L, "Maria", "maria@email.com", "52998224725", current, CREATED_ON);

        customer.updatePersonalData("Maria", "maria@email.com", null);

        assertNull(customer.getAddress());
    }
}
