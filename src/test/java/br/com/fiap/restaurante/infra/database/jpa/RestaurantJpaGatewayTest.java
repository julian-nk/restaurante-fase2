package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.CreateRestaurantInputDto;
import br.com.fiap.restaurante.core.dto.OpeningHoursInputDto;
import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.OpeningHoursFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.RestaurantFactoryImpl;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.OpeningHoursEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.OpeningHoursRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.RestaurantRepository;
import br.com.fiap.restaurante.infra.database.mapper.AddressMapper;
import br.com.fiap.restaurante.infra.database.mapper.RestaurantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestaurantJpaGatewayTest {

    private static final AddressEntity ADDRESS =
            new AddressEntity(5L, "Rua A", "10", null, "Centro", "Campinas", "SP", "13010000");
    private static final RestaurantEntity RESTAURANT =
            new RestaurantEntity(1L, "Cantina", "12345678000199", "Italiana", 7L, 5L);
    private static final OpeningHoursEntity MONDAY_LUNCH =
            new OpeningHoursEntity(20L, 1L, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(15, 0));

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final OpeningHoursFactoryImpl openingHoursFactory = new OpeningHoursFactoryImpl();
    private final RestaurantFactoryImpl restaurantFactory = new RestaurantFactoryImpl(addressFactory,
                                                                                      openingHoursFactory);
    private RestaurantRepository restaurantRepository;
    private AddressRepository addressRepository;
    private OpeningHoursRepository openingHoursRepository;
    private RestaurantJpaGateway gateway;

    @BeforeEach
    void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        addressRepository = mock(AddressRepository.class);
        openingHoursRepository = mock(OpeningHoursRepository.class);
        AddressMapper addressMapper = new AddressMapper(addressFactory);
        gateway = new RestaurantJpaGateway(restaurantRepository, addressRepository, openingHoursRepository,
                                           new RestaurantMapper(restaurantFactory, openingHoursFactory, addressMapper),
                                           addressMapper);
    }

    @Test
    void shouldSaveRestaurantReplacingOpeningHours() {
        Restaurant restaurant = restaurantFactory.create(new CreateRestaurantInputDto("Cantina", "12345678000199",
                                                                                      "Italiana", 7L,
                                                                                      new AddressInputDto("Rua A", "10",
                                                                                                          null,
                                                                                                          "Centro",
                                                                                                          "Campinas",
                                                                                                          StatesEnum.SP,
                                                                                                          "13010000"),
                                                                                      List.of(new OpeningHoursInputDto(
                                                                                              DayOfWeek.MONDAY,
                                                                                              LocalTime.of(11, 0),
                                                                                              LocalTime.of(15, 0)))));
        when(addressRepository.save(any())).thenReturn(ADDRESS);
        when(restaurantRepository.save(any())).thenReturn(RESTAURANT);
        when(openingHoursRepository.saveAll(any())).thenReturn(List.of(MONDAY_LUNCH));

        Restaurant saved = gateway.save(restaurant);

        assertEquals(1L, saved.getId());
        assertEquals(5L, saved.getAddress().getId());
        assertEquals(20L, saved.getOpeningHours().get(0).getId());

        ArgumentCaptor<RestaurantEntity> restaurantCaptor = ArgumentCaptor.forClass(RestaurantEntity.class);
        verify(restaurantRepository).save(restaurantCaptor.capture());
        assertEquals(5L, restaurantCaptor.getValue().getAddressId());
        verify(openingHoursRepository).deleteAllByRestaurantId(1L);
    }

    @Test
    void shouldFindByIdAndAll() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(RESTAURANT));
        when(restaurantRepository.findAll()).thenReturn(List.of(RESTAURANT));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(ADDRESS));
        when(addressRepository.findAllById(List.of(5L))).thenReturn(List.of(ADDRESS));
        when(openingHoursRepository.findByRestaurantId(1L)).thenReturn(List.of(MONDAY_LUNCH));
        when(openingHoursRepository.findByRestaurantIdIn(List.of(1L))).thenReturn(List.of(MONDAY_LUNCH));

        Restaurant found = gateway.findById(1L).orElseThrow();
        List<Restaurant> all = gateway.findAll();

        assertEquals("Cantina", found.getName());
        assertEquals(DayOfWeek.MONDAY, found.getOpeningHours().get(0).getDayOfWeek());
        assertEquals(1, all.size());
        assertEquals("Campinas", all.get(0).getAddress().getCity());
        assertTrue(gateway.findById(2L).isEmpty());
    }

    @Test
    void shouldReturnEmptyListWithoutQueryingChildren() {
        when(restaurantRepository.findAll()).thenReturn(List.of());

        assertTrue(gateway.findAll().isEmpty());
        verifyNoInteractions(addressRepository, openingHoursRepository);
    }

    @Test
    void shouldDelegateExistenceChecks() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(restaurantRepository.existsByCnpj("12345678000199")).thenReturn(true);
        when(restaurantRepository.existsByOwnerId(7L)).thenReturn(true);

        assertTrue(gateway.existsById(1L));
        assertTrue(gateway.existsByCnpj("12345678000199"));
        assertTrue(gateway.existsByOwnerId(7L));
    }

    @Test
    void shouldDeleteRestaurantOpeningHoursAndAddress() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(RESTAURANT));

        gateway.deleteById(1L);

        verify(openingHoursRepository).deleteAllByRestaurantId(1L);
        verify(restaurantRepository).delete(RESTAURANT);
        verify(addressRepository).deleteById(5L);
    }

    @Test
    void shouldWrapRepositoryFailures() {
        when(restaurantRepository.existsByCnpj(any())).thenThrow(new IllegalStateException("falha"));

        assertThrows(RepositoryAccessException.class, () -> gateway.existsByCnpj("12345678000199"));
    }
}
