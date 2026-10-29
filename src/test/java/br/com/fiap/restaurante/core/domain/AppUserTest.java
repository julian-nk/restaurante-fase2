package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

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
        assertTrue(user.getUserTypeIds().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "maria silva", "maria@silva"})
    void shouldRejectInvalidLogin(String login) {
        assertThrows(InvalidFieldException.class,
                () -> factory.create(new CreateAppUserInputDto(1L, login, "senha123"), "hash"));
    }

    @Test
    void shouldRejectInvalidPersonIdAndMissingHash() {
        assertThrows(InvalidFieldException.class, () -> factory.restore(1L, 0L, "maria", "hash", Set.of()));
        assertThrows(InvalidFieldException.class, () -> factory.restore(1L, 1L, "maria", " ", Set.of()));
    }

    @Test
    void shouldChangePasswordHash() {
        AppUser user = factory.restore(1L, 1L, "maria", "hash-antigo", Set.of(2L));

        user.changePasswordHash("hash-novo");

        assertEquals("hash-novo", user.getPasswordHash());
        assertEquals(Set.of(2L), user.getUserTypeIds());
        assertThrows(InvalidFieldException.class, () -> user.changePasswordHash(null));
    }

    @Test
    void shouldRestoreUserWithItsTypesInOrder() {
        AppUser user = factory.restore(1L, 1L, "maria", "hash", Set.of(3L, 1L, 2L));

        assertEquals(List.of(1L, 2L, 3L), List.copyOf(user.getUserTypeIds()));
        assertTrue(user.hasUserType(2L));
        assertFalse(user.hasUserType(4L));
        assertTrue(factory.restore(1L, 1L, "maria", "hash", null).getUserTypeIds().isEmpty());
    }

    @Test
    void shouldAddAndRemoveUserTypesWithoutDuplicates() {
        AppUser user = factory.restore(1L, 1L, "maria", "hash", Set.of(1L));

        assertTrue(user.addUserType(2L));
        assertFalse(user.addUserType(2L));
        assertEquals(Set.of(1L, 2L), user.getUserTypeIds());

        assertTrue(user.removeUserType(1L));
        assertFalse(user.removeUserType(1L));
        assertEquals(Set.of(2L), user.getUserTypeIds());
    }

    @Test
    void shouldRejectInvalidUserTypeIds() {
        AppUser user = factory.restore(1L, 1L, "maria", "hash", Set.of());

        assertThrows(InvalidFieldException.class, () -> user.addUserType(null));
        assertThrows(InvalidFieldException.class, () -> user.addUserType(0L));
        assertThrows(InvalidFieldException.class, () -> factory.restore(1L, 1L, "maria", "hash", Set.of(-1L)));
    }

    @Test
    void shouldNotLetTheTypesBeChangedFromOutside() {
        AppUser user = factory.restore(1L, 1L, "maria", "hash", Set.of(1L));

        assertThrows(UnsupportedOperationException.class, () -> user.getUserTypeIds().add(2L));
        assertThrows(UnsupportedOperationException.class, () -> user.getUserTypeIds().clear());
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
