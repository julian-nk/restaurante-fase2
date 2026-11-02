package br.com.fiap.restaurante.infra.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiAccessRulesTest {

    private static Rule ruleFor(HttpMethod method, String path) {
        return ApiAccessRules.ruleFor(method, path).orElseThrow();
    }

    @Test
    void anyAuthenticatedUserShouldReadRestaurantsAndMenu() {
        assertTrue(ruleFor(HttpMethod.GET, "/api/restaurants").allowsAnyAuthenticatedUser());
        assertTrue(ruleFor(HttpMethod.GET, "/api/restaurants/{id}").allowsAnyAuthenticatedUser());
        assertTrue(ruleFor(HttpMethod.GET, "/api/menu-items/restaurant/{restaurantId}").allowsAnyAuthenticatedUser());
    }

    @Test
    void onlyRestaurantOwnerOrAdminShouldWriteRestaurantsAndMenu() {
        List<UserTypeEnum> expected = List.of(UserTypeEnum.ADMIN, UserTypeEnum.RESTAURANT_OWNER);

        assertEquals(expected, ruleFor(HttpMethod.POST, "/api/menu-items").userTypeCodes());
        assertEquals(expected, ruleFor(HttpMethod.PUT, "/api/menu-items/{id}").userTypeCodes());
        assertEquals(expected, ruleFor(HttpMethod.DELETE, "/api/menu-items/{id}").userTypeCodes());
        assertEquals(expected, ruleFor(HttpMethod.POST, "/api/restaurants").userTypeCodes());
        assertEquals(expected, ruleFor(HttpMethod.DELETE, "/api/restaurants/{id}").userTypeCodes());
    }

    @Test
    void anyAuthenticatedUserShouldChangePassword() {
        assertTrue(ruleFor(HttpMethod.PATCH, "/api/app-users/{id}/password").allowsAnyAuthenticatedUser());
        assertTrue(ruleFor(HttpMethod.PATCH, "/api/app-users/7/password").allowsAnyAuthenticatedUser());
    }

    @Test
    void onlyAdminShouldReachTheRestOfTheApi() {
        List<UserTypeEnum> admin = List.of(UserTypeEnum.ADMIN);

        assertEquals(admin, ruleFor(HttpMethod.GET, "/api/user-types").userTypeCodes());
        assertEquals(admin, ruleFor(HttpMethod.POST, "/api/app-users/{appUserId}/types/{userTypeId}").userTypeCodes());
        assertEquals(admin, ruleFor(HttpMethod.GET, "/api/app-users/{id}").userTypeCodes());
        assertEquals(admin, ruleFor(HttpMethod.GET, "/api/persons").userTypeCodes());
        assertEquals(admin, ruleFor(HttpMethod.PUT, "/api/customers/{id}").userTypeCodes());
        assertEquals(admin, ruleFor(HttpMethod.DELETE, "/api/restaurant-owners/{id}").userTypeCodes());
    }

    @Test
    void documentationShouldHaveNoRule() {
        assertTrue(ApiAccessRules.ruleFor(HttpMethod.GET, "/my-docs").isEmpty());
        assertTrue(ApiAccessRules.ruleFor(HttpMethod.GET, "/api-docs").isEmpty());
    }

    @Test
    void shouldConvertUserTypeCodesToSpringRoles() {
        assertEquals("RESTAURANT_OWNER", ApiAccessRules.roleOf("restaurant_owner"));
        assertArrayEquals(new String[]{"ADMIN", "RESTAURANT_OWNER"},
                          ruleFor(HttpMethod.POST, "/api/menu-items").roles());
    }

    @Test
    void ruleShouldNotBeChangedFromOutside() {
        List<UserTypeEnum> codes = ruleFor(HttpMethod.GET, "/api/persons").userTypeCodes();

        assertThrows(UnsupportedOperationException.class, () -> codes.add(UserTypeEnum.CUSTOMER));
    }
}
