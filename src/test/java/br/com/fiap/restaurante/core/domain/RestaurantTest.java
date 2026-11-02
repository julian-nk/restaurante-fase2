package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.OpeningHoursFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.RestaurantFactoryImpl;
import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.dto.OpeningHoursInputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    private final OpeningHoursFactoryImpl openingHoursFactory = new OpeningHoursFactoryImpl();
    private final RestaurantFactoryImpl factory = new RestaurantFactoryImpl(new AddressFactoryImpl(), openingHoursFactory);

    static OpeningHoursInputDto hours(DayOfWeek day, String opening, String closing) {
        return new OpeningHoursInputDto(day, LocalTime.parse(opening), LocalTime.parse(closing));
    }

    static CreateRestaurantInputDto input(List<OpeningHoursInputDto> openingHours) {
        return new CreateRestaurantInputDto("Cantina", "12.345.678/0001-99", "Italiana", 7L,
                AddressTest.validInput(), openingHours);
    }

    private OpeningHours shift(DayOfWeek day, String opening, String closing) {
        return openingHoursFactory.create(hours(day, opening, closing));
    }

    @Test
    void shouldCreateRestaurantSortingOpeningHours() {
        Restaurant restaurant = factory.create(input(List.of(
                hours(FRIDAY, "18:00", "23:00"),
                hours(MONDAY, "18:00", "23:00"),
                hours(MONDAY, "11:00", "15:00"))));

        assertNull(restaurant.getId());
        assertEquals("Cantina", restaurant.getName());
        assertEquals("12345678000199", restaurant.getCnpj());
        assertEquals("Italiana", restaurant.getCuisineType());
        assertEquals(7L, restaurant.getOwnerId());
        assertEquals(StatesEnum.SP, restaurant.getAddress().getState());
        assertEquals(List.of(MONDAY, MONDAY, FRIDAY),
                restaurant.getOpeningHours().stream().map(OpeningHours::getDayOfWeek).toList());
        assertEquals(LocalTime.of(11, 0), restaurant.getOpeningHours().get(0).getOpeningTime());
    }

    @Test
    void shouldRequireAtLeastOneOpeningHour() {
        assertThrows(InvalidFieldException.class, () -> factory.create(input(List.of())));
        assertThrows(InvalidFieldException.class, () -> factory.create(input(null)));
    }

    @Test
    void shouldRequireAddressAndValidCnpj() {
        var withoutAddress = new CreateRestaurantInputDto("Cantina", "12345678000199", "Italiana", 7L, null,
                List.of(hours(MONDAY, "11:00", "15:00")));
        var invalidCnpj = new CreateRestaurantInputDto("Cantina", "123", "Italiana", 7L, AddressTest.validInput(),
                List.of(hours(MONDAY, "11:00", "15:00")));

        assertThrows(InvalidFieldException.class, () -> factory.create(withoutAddress));
        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(invalidCnpj));
        assertEquals("O campo CNPJ deve conter 14 dígitos.", exception.getMessage());
    }

    @Test
    void shouldRejectOverlappingShiftsOnSameDay() {
        var exception = assertThrows(InvalidFieldException.class, () -> factory.create(input(List.of(
                hours(MONDAY, "11:00", "15:00"),
                hours(MONDAY, "14:00", "18:00")))));

        assertTrue(exception.getMessage().startsWith("Os horários de funcionamento não podem se sobrepor"));
    }

    @Test
    void shouldRejectOvernightShiftOverlappingNextDay() {
        assertThrows(InvalidFieldException.class, () -> factory.create(input(List.of(
                hours(FRIDAY, "18:00", "02:00"),
                hours(SATURDAY, "01:00", "05:00")))));
    }

    @Test
    void shouldRejectSundayOvernightShiftOverlappingMonday() {
        assertThrows(InvalidFieldException.class, () -> factory.create(input(List.of(
                hours(SUNDAY, "22:00", "03:00"),
                hours(MONDAY, "02:00", "06:00")))));
    }

    @Test
    void shouldAcceptAdjacentShifts() {
        Restaurant restaurant = factory.create(input(List.of(
                hours(MONDAY, "11:00", "15:00"),
                hours(MONDAY, "15:00", "23:00"),
                hours(TUESDAY, "18:00", "02:00"),
                hours(WEDNESDAY, "02:00", "05:00"))));

        assertEquals(4, restaurant.getOpeningHours().size());
    }

    @Test
    void openingHoursShouldRejectEqualTimesAndMissingFields() {
        assertThrows(InvalidFieldException.class, () -> shift(MONDAY, "10:00", "10:00"));
        assertThrows(InvalidFieldException.class, () -> openingHoursFactory.create(null));
        assertThrows(InvalidFieldException.class,
                () -> openingHoursFactory.create(new OpeningHoursInputDto(null, LocalTime.NOON, LocalTime.MIDNIGHT)));
    }

    @Test
    void openingHoursShouldKnowWhenItClosesNextDay() {
        assertTrue(shift(FRIDAY, "18:00", "02:00").closesNextDay());
        assertFalse(shift(FRIDAY, "11:00", "15:00").closesNextDay());
        assertEquals(4L, openingHoursFactory.restore(4L, MONDAY, LocalTime.NOON, LocalTime.MIDNIGHT).getId());
    }

    @Test
    void shouldUpdateDataAddressAndOpeningHours() {
        Restaurant restaurant = factory.create(input(List.of(hours(MONDAY, "11:00", "15:00"))));

        restaurant.update("Cantina Nova", "Italiana contemporânea", 8L);
        restaurant.updateAddress("Rua B", "2", null, "Centro", "Campinas", StatesEnum.SP, "13010000");
        restaurant.replaceOpeningHours(new ArrayList<>(List.of(shift(TUESDAY, "18:00", "23:00"))));

        assertEquals("Cantina Nova", restaurant.getName());
        assertEquals(8L, restaurant.getOwnerId());
        assertEquals("Rua B", restaurant.getAddress().getStreet());
        assertEquals(TUESDAY, restaurant.getOpeningHours().get(0).getDayOfWeek());
        assertThrows(InvalidFieldException.class, () -> restaurant.update("Cantina", "Italiana", null));
        assertThrows(InvalidFieldException.class, () -> restaurant.replaceOpeningHours(List.of()));
    }

    @Test
    void shouldRestoreRestaurant() {
        Address address = new AddressFactoryImpl().restore(3L, "Rua A", "1", null, "Centro", "Campinas", "SP", "13010000");
        Restaurant restaurant = factory.restore(1L, "Cantina", "12345678000199", "Italiana", 7L, address,
                List.of(openingHoursFactory.restore(10L, MONDAY, LocalTime.of(11, 0), LocalTime.of(15, 0))));

        assertEquals(1L, restaurant.getId());
        assertEquals(3L, restaurant.getAddress().getId());
        assertEquals(10L, restaurant.getOpeningHours().get(0).getId());
    }
}
