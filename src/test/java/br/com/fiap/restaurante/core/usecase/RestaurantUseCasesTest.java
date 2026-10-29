package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.*;
import br.com.fiap.restaurante.core.exception.CnpjAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.RestaurantNotFoundException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.OpeningHoursFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.RestaurantFactoryImpl;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestaurantUseCasesTest {

    private static final AddressInputDto ADDRESS =
            new AddressInputDto("Rua A", "10", null, "Centro", "Campinas", StatesEnum.SP, "13010000");
    private static final List<OpeningHoursInputDto> HOURS =
            List.of(new OpeningHoursInputDto(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(15, 0)));

    private final OpeningHoursFactoryImpl openingHoursFactory = new OpeningHoursFactoryImpl();
    private final RestaurantFactoryImpl factory = new RestaurantFactoryImpl(new AddressFactoryImpl(),
                                                                            openingHoursFactory);
    private RestaurantGateway gateway;
    private RestaurantOwnerGateway ownerGateway;

    @BeforeEach
    void setUp() {
        gateway = mock(RestaurantGateway.class);
        ownerGateway = mock(RestaurantOwnerGateway.class);
        when(ownerGateway.existsById(7L)).thenReturn(true);
        when(gateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Restaurant persisted() {
        Restaurant created = factory.create(
                new CreateRestaurantInputDto("Cantina", "12345678000199", "Italiana", 7L, ADDRESS, HOURS));
        return factory.restore(1L, created.getName(), created.getCnpj(), created.getCuisineType(), created.getOwnerId(),
                               created.getAddress(), created.getOpeningHours());
    }

    @Nested
    class Create {

        private CreateRestaurantUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new CreateRestaurantUseCaseImpl(gateway, ownerGateway, factory);
        }

        @Test
        void shouldCreateRestaurant() {
            RestaurantOutputDto output = useCase.execute(
                    new CreateRestaurantInputDto("Cantina", "12.345.678/0001-99", "Italiana", 7L, ADDRESS, HOURS));

            assertEquals("12345678000199", output.cnpj());
            assertEquals("Campinas", output.address().city());
            assertEquals(DayOfWeek.MONDAY, output.openingHours().get(0).dayOfWeek());
        }

        @Test
        void shouldRejectUnknownOwner() {
            assertThrows(RestaurantOwnerNotFoundException.class, () -> useCase.execute(
                    new CreateRestaurantInputDto("Cantina", "12345678000199", "Italiana", 8L, ADDRESS, HOURS)));
            verify(gateway, never()).save(any());
        }

        @Test
        void shouldRejectDuplicatedCnpj() {
            when(gateway.existsByCnpj("12345678000199")).thenReturn(true);

            assertThrows(CnpjAlreadyRegisteredException.class, () -> useCase.execute(
                    new CreateRestaurantInputDto("Cantina", "12345678000199", "Italiana", 7L, ADDRESS, HOURS)));
        }

        @Test
        void shouldValidateBeforeQueryingGateways() {
            assertThrows(InvalidFieldException.class, () -> useCase.execute(
                    new CreateRestaurantInputDto("Cantina", "12345678000199", "Italiana", 7L, ADDRESS, List.of())));
            verifyNoInteractions(ownerGateway);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAndFindRestaurants() {
            when(gateway.findAll()).thenReturn(List.of(persisted()));
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted()));

            assertEquals(1, new GetRestaurantsUseCaseImpl(gateway).execute().size());
            assertEquals("Cantina", new GetRestaurantByIdUseCaseImpl(gateway).execute(1L).name());
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(9L)).thenReturn(Optional.empty());

            assertThrows(RestaurantNotFoundException.class,
                         () -> new GetRestaurantByIdUseCaseImpl(gateway).execute(9L));
        }
    }

    @Nested
    class Update {

        private UpdateRestaurantUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateRestaurantUseCaseImpl(gateway, ownerGateway, openingHoursFactory);
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted()));
        }

        @Test
        void shouldUpdateRestaurant() {
            var newHours = List.of(new OpeningHoursInputDto(DayOfWeek.FRIDAY, LocalTime.of(18, 0), LocalTime.of(2, 0)));

            RestaurantOutputDto output = useCase.execute(
                    new UpdateRestaurantInputDto(1L, "Cantina Nova", "Italiana", 7L,
                                                 new AddressInputDto("Rua B", "20", null, "Centro", "Campinas",
                                                                     StatesEnum.SP,
                                                                     "13010000"), newHours));

            assertEquals(1L, output.id());
            assertEquals("Cantina Nova", output.name());
            assertEquals("Rua B", output.address().street());
            assertEquals(DayOfWeek.FRIDAY, output.openingHours().get(0).dayOfWeek());
        }

        @Test
        void shouldRequireAddressAndOpeningHours() {
            assertThrows(InvalidFieldException.class, () -> useCase.execute(
                    new UpdateRestaurantInputDto(1L, "Cantina", "Italiana", 7L, null, HOURS)));
            assertThrows(InvalidFieldException.class, () -> useCase.execute(
                    new UpdateRestaurantInputDto(1L, "Cantina", "Italiana", 7L, ADDRESS, null)));
            verify(gateway, never()).save(any());
        }

        @Test
        void shouldRejectUnknownOwner() {
            assertThrows(RestaurantOwnerNotFoundException.class, () -> useCase.execute(
                    new UpdateRestaurantInputDto(1L, "Cantina", "Italiana", 8L, ADDRESS, HOURS)));
        }

        @Test
        void shouldThrowWhenNotFound() {
            assertThrows(RestaurantNotFoundException.class, () -> useCase.execute(
                    new UpdateRestaurantInputDto(9L, "Cantina", "Italiana", 7L, ADDRESS, HOURS)));
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteRestaurant() {
            when(gateway.existsById(1L)).thenReturn(true);

            new DeleteRestaurantUseCaseImpl(gateway).execute(1L);

            verify(gateway).deleteById(1L);
        }

        @Test
        void shouldThrowWhenNotFound() {
            assertThrows(RestaurantNotFoundException.class, () -> new DeleteRestaurantUseCaseImpl(gateway).execute(9L));
        }
    }
}
