
package br.com.fiap.restaurante.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTypeTest {

    @Test
    void shouldCreateUserTypeWithNameAndCode() {
        UserType userType = new UserType("Cliente", "customer");

        assertEquals("Cliente", userType.getName());
        assertEquals("customer", userType.getCode());
        assertNull(userType.getId());
    }

    @Test
    void shouldUpdateNameAndCode() {
        UserType userType = new UserType("Cliente", "customer");

        userType.update("Administrador", "admin");

        assertEquals("Administrador", userType.getName());
        assertEquals("admin", userType.getCode());
    }

    @Test
    void shouldAllowUpdatingNameUsingSetter() {
        UserType userType = new UserType("Cliente", "customer");

        userType.setName("Administrador");

        assertEquals("Administrador", userType.getName());
    }

    @Test
    void shouldAllowUpdatingCodeUsingSetter() {
        UserType userType = new UserType("Cliente", "customer");

        userType.setCode("admin");

        assertEquals("admin", userType.getCode());
    }

    @Test
    void shouldAllowUpdatingNameAndCodeToNull() {
        UserType userType = new UserType("Cliente", "customer");

        userType.update(null, null);

        assertNull(userType.getName());
        assertNull(userType.getCode());
    }
}
