package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    private final AppUserFactoryImpl factory = new AppUserFactoryImpl();

    @Test
    void shouldCreateUserWithLowercaseLogin() {
        AppUser user = factory.create(new CreateAppUserInputDto(1L, " Maria.Silva ", "senha123"), "hash");

        assertNull(user.getId());
        assertEquals(1L, user.getPersonId());
        assertEquals("maria.silva", user.getLogin());
        assertEquals("hash", user.getPasswordHash());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "maria silva", "maria@silva"})
    void shouldRejectInvalidLogin(String login) {
        assertThrows(InvalidFieldException.class,
                () -> factory.create(new CreateAppUserInputDto(1L, login, "senha123"), "hash"));
    }

    @Test
    void shouldRejectInvalidPersonIdAndMissingHash() {
        assertThrows(InvalidFieldException.class, () -> factory.restore(1L, 0L, "maria", "hash"));
        assertThrows(InvalidFieldException.class, () -> factory.restore(1L, 1L, "maria", " "));
    }

    @Test
    void shouldChangePasswordHash() {
        AppUser user = factory.restore(1L, 1L, "maria", "hash-antigo");

        user.changePasswordHash("hash-novo");

        assertEquals("hash-novo", user.getPasswordHash());
        assertThrows(InvalidFieldException.class, () -> user.changePasswordHash(null));
    }

    @Test
    void passwordPolicyShouldAcceptStrongPassword() {
        assertDoesNotThrow(() -> PasswordPolicy.validate("senhaForte1"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc123", "somenteletras", "1234567890"})
    void passwordPolicyShouldRejectWeakPasswords(String password) {
        assertThrows(InvalidFieldException.class, () -> PasswordPolicy.validate(password));
    }

    @Test
    void passwordPolicyShouldRejectPasswordLongerThanBcryptLimit() {
        assertThrows(InvalidFieldException.class, () -> PasswordPolicy.validate("a1".repeat(37)));
    }
}
