package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.CustomerFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.RestaurantOwnerFactoryImpl;
import br.com.fiap.restaurante.infra.database.jpa.PersonPersistence.PersonRecord;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.CustomerEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantOwnerEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AppUserRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.CustomerRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.RestaurantOwnerRepository;
import br.com.fiap.restaurante.infra.database.mapper.AddressMapper;
import br.com.fiap.restaurante.infra.database.mapper.AppUserMapper;
import br.com.fiap.restaurante.infra.database.mapper.CustomerMapper;
import br.com.fiap.restaurante.infra.database.mapper.RestaurantOwnerMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleJpaGatewaysTest {

    private static final OffsetDateTime CREATED_ON = OffsetDateTime.parse("2026-01-10T10:00:00-03:00");
    private static final PersonRecord PERSON =
            new PersonRecord(new PersonEntity(5L, "Maria", "maria@email.com", "52998224725", null), null);

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final AddressMapper addressMapper = new AddressMapper(addressFactory);

    @Test
    void appUserGatewayShouldMapBothWays() {
        AppUserRepository repository = mock(AppUserRepository.class);
        AppUserFactoryImpl factory = new AppUserFactoryImpl();
        AppUserJpaGateway gateway = new AppUserJpaGateway(repository, new AppUserMapper(factory));
        AppUserEntity entity = new AppUserEntity(1L, 10L, "maria", "hash");
        when(repository.save(any())).thenReturn(entity);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.existsByPersonId(10L)).thenReturn(true);
        when(repository.existsByLoginIgnoreCase("maria")).thenReturn(true);

        AppUser saved = gateway.save(factory.restore(null, 10L, "maria", "hash"));

        assertEquals(1L, saved.getId());
        assertEquals("hash", gateway.findById(1L).orElseThrow().getPasswordHash());
        assertEquals(1, gateway.findAll().size());
        assertTrue(gateway.existsByPersonId(10L));
        assertTrue(gateway.existsByLogin("maria"));
        gateway.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void customerGatewayShouldSavePersonAndRoleTogether() {
        CustomerRepository repository = mock(CustomerRepository.class);
        PersonPersistence persistence = mock(PersonPersistence.class);
        CustomerFactoryImpl factory = new CustomerFactoryImpl(addressFactory);
        CustomerJpaGateway gateway = new CustomerJpaGateway(repository, persistence, new CustomerMapper(factory, addressMapper));
        CustomerEntity entity = new CustomerEntity(5L, CREATED_ON);
        when(persistence.save(any())).thenReturn(PERSON);
        when(persistence.findById(5L)).thenReturn(Optional.of(PERSON));
        when(persistence.findAllById(List.of(5L))).thenReturn(Map.of(5L, PERSON));
        when(repository.save(any())).thenReturn(entity);
        when(repository.findById(5L)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.existsById(5L)).thenReturn(true);

        Customer saved = gateway.save(factory.restore(null, "Maria", "maria@email.com", "52998224725", null, CREATED_ON));

        ArgumentCaptor<CustomerEntity> role = ArgumentCaptor.forClass(CustomerEntity.class);
        verify(repository).save(role.capture());
        assertEquals(5L, role.getValue().getPersonId());
        assertEquals(5L, saved.getId());
        assertEquals("Maria", saved.getName());
        assertEquals(CREATED_ON, saved.getCreatedOn());
        assertEquals("maria@email.com", gateway.findById(5L).orElseThrow().getEmail());
        assertTrue(gateway.findById(6L).isEmpty());
        assertEquals(1, gateway.findAll().size());
        assertTrue(gateway.existsById(5L));
        gateway.deleteById(5L);
        verify(repository).deleteById(5L);
    }

    @Test
    void restaurantOwnerGatewayShouldSavePersonAndRoleTogether() {
        RestaurantOwnerRepository repository = mock(RestaurantOwnerRepository.class);
        PersonPersistence persistence = mock(PersonPersistence.class);
        RestaurantOwnerFactoryImpl factory = new RestaurantOwnerFactoryImpl(addressFactory);
        RestaurantOwnerJpaGateway gateway = new RestaurantOwnerJpaGateway(repository, persistence,
                new RestaurantOwnerMapper(factory, addressMapper));
        RestaurantOwnerEntity entity = new RestaurantOwnerEntity(5L, CREATED_ON, "contato@cantina.com");
        when(persistence.save(any())).thenReturn(PERSON);
        when(persistence.findById(5L)).thenReturn(Optional.of(PERSON));
        when(persistence.findAllById(List.of(5L))).thenReturn(Map.of(5L, PERSON));
        when(repository.save(any())).thenReturn(entity);
        when(repository.findById(5L)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.existsById(5L)).thenReturn(true);

        RestaurantOwner saved = gateway.save(factory.restore(
                null, "Maria", "maria@email.com", "52998224725", null, CREATED_ON, "contato@cantina.com"));

        ArgumentCaptor<RestaurantOwnerEntity> role = ArgumentCaptor.forClass(RestaurantOwnerEntity.class);
        verify(repository).save(role.capture());
        assertEquals(5L, role.getValue().getPersonId());
        assertEquals(5L, saved.getId());
        assertEquals("contato@cantina.com", saved.getBusinessEmail());
        assertEquals("Maria", gateway.findById(5L).orElseThrow().getName());
        assertEquals(1, gateway.findAll().size());
        assertTrue(gateway.existsById(5L));
        gateway.deleteById(5L);
        verify(repository).deleteById(5L);
    }
}
