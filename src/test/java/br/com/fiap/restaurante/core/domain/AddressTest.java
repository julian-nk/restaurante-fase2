package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private final AddressFactoryImpl factory = new AddressFactoryImpl();

    static AddressInputDto validInput() {
        return new AddressInputDto("Av. Paulista", "1000", "Sala 5", "Bela Vista", "São Paulo",
                                   StatesEnum.fromValue("sp"), "01310-100");
    }

    private static AddressInputDto withState(String state) {
        return new AddressInputDto("Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.fromValue(state),
                                   "01310100");
    }

    private static AddressInputDto withZipCode(String zipCode) {
        return new AddressInputDto("Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, zipCode);
    }

    @Test
    void shouldCreateAddressNormalizingStateAndZipCode() {
        Address address = factory.create(validInput());

        assertNull(address.getId());
        assertEquals("Av. Paulista", address.getStreet());
        assertEquals("1000", address.getNumber());
        assertEquals("Sala 5", address.getComplement());
        assertEquals("Bela Vista", address.getNeighborhood());
        assertEquals("São Paulo", address.getCity());
        assertEquals("SP", address.getState().getValue());
        assertEquals("01310100", address.getZipCode());
    }

    @Test
    void shouldTreatBlankComplementAsNull() {
        Address address = factory.restore(3L, "Rua A", "1", "  ", "Centro", "Campinas", "SP", "13010000");

        assertEquals(3L, address.getId());
        assertNull(address.getComplement());
    }

    @Test
    void shouldRejectMissingInput() {
        assertThrows(InvalidFieldException.class, () -> factory.create(null));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"XX", "S", "SPP", "1A"})
    void shouldRejectInvalidState(String state) {
        assertThrows(InvalidFieldException.class, () -> factory.create(withState(state)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1234567", "123456789", "01310-10A"})
    void shouldRejectInvalidZipCode(String zipCode) {
        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(withZipCode(zipCode)));
        assertTrue(exception.getMessage().contains("CEP"));
    }

    @Test
    void shouldRejectTooLongStreet() {
        var input = new AddressInputDto("r".repeat(151), "1", null, "Centro", "Campinas", StatesEnum.SP, "13010000");

        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(input));
        assertEquals("O campo logradouro deve ter no máximo 150 caracteres.", exception.getMessage());
    }

    @Test
    void shouldUpdateKeepingIdAndRejectInvalidUpdateWithoutChangingState() {
        Address address = factory.restore(3L, "Rua A", "1", null, "Centro", "Campinas", "SP", "13010000");

        address.update("Rua B", "2", "Casa", "Jardim", "Santos", StatesEnum.fromValue("sp"), "11010-000");
        assertEquals(3L, address.getId());
        assertEquals("Rua B", address.getStreet());
        assertEquals("11010000", address.getZipCode());

        assertThrows(InvalidFieldException.class,
                     () -> address.update("Rua C", "", null, "Jardim", "Santos", StatesEnum.SP, "11010000"));
        assertEquals("Rua B", address.getStreet());
    }
}
