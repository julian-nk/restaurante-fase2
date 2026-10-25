
package br.com.fiap.restaurante.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void shouldExecuteProtectedConstructor() throws Exception {
        var constructor = MenuItem.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        MenuItem menuItem = constructor.newInstance();

        assertNotNull(menuItem);
        assertNull(menuItem.getId());
        assertNull(menuItem.getRestaurantId());
        assertNull(menuItem.getName());
        assertNull(menuItem.getDescription());
        assertNull(menuItem.getPrice());
        assertFalse(menuItem.isDineInOnly());
        assertNull(menuItem.getPhotoUrl());
    }
}
