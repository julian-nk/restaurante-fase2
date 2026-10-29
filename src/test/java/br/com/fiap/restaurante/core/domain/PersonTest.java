package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.PersonFactoryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private static final AddressInputDto ADDRESS = new AddressInputDto(
            "Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310-100");

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final PersonFactoryImpl personFactory = new PersonFactoryImpl(addressFactory);

    @Test
    void shouldCreatePersonWithoutRole() {
        Person person = personFactory.create(
                new CreatePersonInputDto("Ana", "Ana@Email.com", "390.533.447-05", ADDRESS));

        assertNull(person.getId());
        assertEquals("Ana", person.getName());
        assertEquals("ana@email.com", person.getEmail());
        assertEquals("39053344705", person.getCpf());
        assertEquals("Bela Vista", person.getAddress().getNeighborhood());
        assertFalse(person instanceof Customer);
        assertFalse(person instanceof RestaurantOwner);
    }

    @Test
    void shouldCreatePersonWithoutAddress() {
        assertNull(personFactory.create(new CreatePersonInputDto("Ana", "ana@email.com", "39053344705", null))
                .getAddress());
    }

    @Test
    void shouldRestoreStoredPerson() {
        Address address = addressFactory.restore(9L, "Rua A", "1", null, "Centro", "Campinas", "SP", "13010000");

        Person person = personFactory.restore(1L, "Ana", "ana@email.com", "39053344705", address);

        assertEquals(1L, person.getId());
        assertSame(address, person.getAddress());
    }

    @Test
    void shouldRejectInvalidPersonalData() {
        assertThrows(InvalidFieldException.class, () -> personFactory.create(
                new CreatePersonInputDto(" ", "ana@email.com", "39053344705", null)));
        assertThrows(InvalidFieldException.class, () -> personFactory.create(
                new CreatePersonInputDto("Ana", "ana-sem-arroba", "39053344705", null)));
        assertThrows(InvalidFieldException.class, () -> personFactory.create(
                new CreatePersonInputDto("Ana", "ana@email.com", "390.533", null)));
        assertThrows(InvalidFieldException.class, () -> personFactory.create(
                new CreatePersonInputDto("A".repeat(151), "ana@email.com", "39053344705", null)));
    }

    @Test
    void shouldUpdatePersonalDataOnlyWhenEverythingIsValid() {
        Person person = personFactory.restore(1L, "Ana", "ana@email.com", "39053344705", null);

        assertThrows(InvalidFieldException.class, () -> person.update("Ana Pereira", "sem-arroba"));
        assertEquals("Ana", person.getName());

        person.updatePersonalData("Ana Pereira", "ana.pereira@email.com", addressFactory.create(ADDRESS));
        assertEquals("Ana Pereira", person.getName());
        assertEquals("Av. Paulista", person.getAddress().getStreet());

        person.updatePersonalData("Ana Pereira", "ana.pereira@email.com", null);
        assertNull(person.getAddress());
    }
}
