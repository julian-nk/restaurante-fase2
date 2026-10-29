package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.CustomerFactoryImpl;
import br.com.fiap.restaurante.infra.database.jpa.PersonPersistence.PersonRecord;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import br.com.fiap.restaurante.infra.database.mapper.AddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonPersistenceTest {

    private static final AddressEntity ADDRESS =
            new AddressEntity(5L, "Rua A", "10", null, "Centro", "Campinas", "SP", "13010000");

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final CustomerFactoryImpl customerFactory = new CustomerFactoryImpl(addressFactory);
    private PersonRepository personRepository;
    private AddressRepository addressRepository;
    private PersonPersistence persistence;

    @BeforeEach
    void setUp() {
        personRepository = mock(PersonRepository.class);
        addressRepository = mock(AddressRepository.class);
        persistence = new PersonPersistence(personRepository, addressRepository, new AddressMapper(addressFactory));
        when(addressRepository.save(any())).thenReturn(ADDRESS);
        when(personRepository.save(any())).thenAnswer(invocation -> {
            PersonEntity entity = invocation.getArgument(0);
            return new PersonEntity(entity.getId() == null ? 1L : entity.getId(), entity.getName(),
                    entity.getEmail(), entity.getCpf(), entity.getAddressId());
        });
    }

    private Customer customer(Long id, Address address) {
        return customerFactory.restore(id, "Maria", "maria@email.com", "52998224725", address, OffsetDateTime.now());
    }

    private Address newAddress(Long id) {
        return addressFactory.restore(id, "Rua A", "10", null, "Centro", "Campinas", "SP", "13010000");
    }

    private void storedPersonWithAddress(Long addressId) {
        when(personRepository.findById(1L))
                .thenReturn(Optional.of(new PersonEntity(1L, "Maria", "maria@email.com", "52998224725", addressId)));
    }

    @Test
    void shouldCreateNewPersonWithAddress() {
        PersonRecord saved = persistence.save(customer(null, newAddress(null)));

        assertEquals(1L, saved.person().getId());
        assertEquals(5L, saved.person().getAddressId());
        assertEquals(5L, saved.address().getId());
        verify(personRepository, never()).findById(any());
        verify(addressRepository, never()).deleteById(any());
    }

    @Test
    void shouldDeleteOldAddressWhenItIsRemoved() {
        storedPersonWithAddress(5L);

        PersonRecord saved = persistence.save(customer(1L, null));

        assertNull(saved.address());
        verify(addressRepository, never()).save(any());
        verify(addressRepository).deleteById(5L);
    }

    @Test
    void shouldDeleteOldAddressWhenItIsReplaced() {
        storedPersonWithAddress(4L);

        persistence.save(customer(1L, newAddress(null)));

        verify(addressRepository).deleteById(4L);
    }

    @Test
    void shouldKeepAddressWhenItIsOnlyUpdated() {
        storedPersonWithAddress(5L);

        persistence.save(customer(1L, newAddress(5L)));

        verify(addressRepository, never()).deleteById(any());
    }

    @Test
    void shouldFindByIdWithAndWithoutAddress() {
        storedPersonWithAddress(5L);
        when(personRepository.findById(2L))
                .thenReturn(Optional.of(new PersonEntity(2L, "João", "joao@email.com", "11144477735", null)));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(ADDRESS));

        assertEquals("Campinas", persistence.findById(1L).orElseThrow().address().getCity());
        assertNull(persistence.findById(2L).orElseThrow().address());
        assertTrue(persistence.findById(3L).isEmpty());
    }

    @Test
    void shouldFindSeveralPersonsLoadingAddressesInOneQuery() {
        when(personRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(
                new PersonEntity(1L, "Maria", "maria@email.com", "52998224725", 5L),
                new PersonEntity(2L, "João", "joao@email.com", "11144477735", null)));
        when(addressRepository.findAllById(List.of(5L))).thenReturn(List.of(ADDRESS));

        Map<Long, PersonRecord> persons = persistence.findAllById(List.of(1L, 2L));

        assertEquals(5L, persons.get(1L).address().getId());
        assertNull(persons.get(2L).address());
    }
}
