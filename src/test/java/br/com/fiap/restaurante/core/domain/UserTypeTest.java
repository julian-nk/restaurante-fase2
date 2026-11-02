package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.factory.impl.UserTypeFactoryImpl;
import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTypeTest {

    private final UserTypeFactoryImpl factory = new UserTypeFactoryImpl();

    @Test
    void shouldCreateUserTypeWithTrimmedValuesAndNoId() {
        UserType userType = factory.create(new CreateUserTypeInputDto("  Cliente ", " customer "));

        assertNull(userType.getId());
        assertEquals("Cliente", userType.getName());
        assertEquals("customer", userType.getCode());
    }

    @Test
    void shouldRestoreUserTypeWithId() {
        UserType userType = factory.restore(7L, "Cliente", "customer");

        assertEquals(7L, userType.getId());
        assertEquals("Cliente", userType.getName());
        assertEquals("customer", userType.getCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void shouldRejectBlankName(String name) {
        var input = new CreateUserTypeInputDto(name, "customer");

        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(input));
        assertEquals("O nome do tipo de usuário é obrigatório.", exception.getMessage());
    }

    @Test
    void shouldRejectNameLongerThanLimit() {
        var input = new CreateUserTypeInputDto("a".repeat(UserType.NAME_MAX_LENGTH + 1), "customer");

        assertThrows(InvalidFieldException.class, () -> factory.create(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void shouldRejectBlankCode(String code) {
        var input = new CreateUserTypeInputDto("Cliente", code);

        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(input));
        assertEquals("O código do tipo de usuário é obrigatório.", exception.getMessage());
    }

    @Test
    void shouldRejectCodeLongerThanLimit() {
        var input = new CreateUserTypeInputDto("Cliente", "c".repeat(UserType.CODE_MAX_LENGTH + 1));

        assertThrows(InvalidFieldException.class, () -> factory.create(input));
    }

    @Test
    void shouldUpdateNameAndCode() {
        UserType userType = factory.restore(1L, "Cliente", "customer");

        userType.update(" Administrador ", "admin");

        assertEquals(1L, userType.getId());
        assertEquals("Administrador", userType.getName());
        assertEquals("admin", userType.getCode());
    }

    @Test
    void shouldNotChangeStateWhenUpdateIsInvalid() {
        UserType userType = factory.restore(1L, "Cliente", "customer");

        assertThrows(InvalidFieldException.class, () -> userType.update("", "admin"));

        assertEquals("Cliente", userType.getName());
        assertEquals("customer", userType.getCode());
    }
}
