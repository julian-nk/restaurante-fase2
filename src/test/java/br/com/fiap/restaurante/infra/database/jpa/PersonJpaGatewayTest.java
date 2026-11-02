package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.PersonFactoryImpl;
import br.com.fiap.restaurante.infra.database.jpa.PersonPersistence.PersonRecord;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import br.com.fiap.restaurante.infra.database.mapper.AddressMapper;
import br.com.fiap.restaurante.infra.database.mapper.PersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonJpaGatewayTest {

    private static final AddressEntity ADDRESS =
            new AddressEntity(5L, "Rua A", "10", null, "Centro", "Campinas", "SP", "13010000");
    private static final PersonEntity PERSON = new PersonEntity(1L, "Ana", "ana@email.com", "39053344705", 5L);

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final PersonFactoryImpl personFactory = new PersonFactoryImpl(addressFactory);
    private PersonRepository personRepository;
    private AddressRepository addressRepository;
    private PersonPersistence personPersistence;
    private PersonJpaGateway gateway;

    @BeforeEach
    void setUp() {
        personRepository = mock(PersonRepository.class);
        addressRepository = mock(AddressRepository.class);
        personPersistence = mock(PersonPersistence.class);
        AddressMapper addressMapper = new AddressMapper(addressFactory);
        gateway = new PersonJpaGateway(personRepository, addressRepository, personPersistence,
                new PersonMapper(personFactory, addressMapper));
    }

    @Test
    void shouldSavePersonAndReturnItWithTheGeneratedIds() {
        Person person = personFactory.restore(null, "Ana", "ana@email.com", "39053344705",
                addressFactory.restore(null, "Rua A", "10", null, "Centro", "Campinas", "SP", "13010000"));
        when(personPersistence.save(person)).thenReturn(new PersonRecord(PERSON, ADDRESS));

        Person saved = gateway.save(person);

        assertEquals(1L, saved.getId());
        assertEquals(5L, saved.getAddress().getId());
        verify(personRepository).flush();
    }

    @Test
    void shouldFindByIdWithTheAddress() {
        when(personPersistence.findById(1L)).thenReturn(Optional.of(new PersonRecord(PERSON, ADDRESS)));

        Person person = gateway.findById(1L).orElseThrow();

        assertEquals("Ana", person.getName());
        assertEquals("Campinas", person.getAddress().getCity());
        assertTrue(gateway.findById(2L).isEmpty());
    }

    @Test
    void shouldFindAllPersons() {
        PersonEntity withoutAddress = new PersonEntity(2L, "João", "joao@email.com", "11144477735", null);
        when(personPersistence.findAll()).thenReturn(List.of(
                new PersonRecord(PERSON, ADDRESS), new PersonRecord(withoutAddress, null)));

        List<Person> persons = gateway.findAll();

        assertEquals(List.of(1L, 2L), persons.stream().map(Person::getId).toList());
        assertNull(persons.get(1).getAddress());
    }

    @Test
    void shouldFindIdByCpf() {
        when(personRepository.findIdByCpf("52998224725")).thenReturn(Optional.of(1L));

        assertEquals(Optional.of(1L), gateway.findIdByCpf("52998224725"));
        assertTrue(gateway.findIdByCpf("11144477735").isEmpty());
    }

    @Test
    void shouldCheckEmailAgainstEveryPersonWhenThePersonIsNew() {
        when(personRepository.existsByEmailIgnoreCase("maria@email.com")).thenReturn(true);

        assertTrue(gateway.isEmailUsedByAnotherPerson("maria@email.com", null));
        verify(personRepository, never()).existsByEmailIgnoreCaseAndIdNot(any(), any());
    }

    @Test
    void shouldIgnoreThePersonItselfWhenCheckingEmail() {
        when(personRepository.existsByEmailIgnoreCaseAndIdNot("maria@email.com", 1L)).thenReturn(false);

        assertFalse(gateway.isEmailUsedByAnotherPerson("maria@email.com", 1L));
        verify(personRepository, never()).existsByEmailIgnoreCase(any());
    }

    @Test
    void shouldCheckExistence() {
        when(personRepository.existsById(1L)).thenReturn(true);

        assertTrue(gateway.existsById(1L));
        assertFalse(gateway.existsById(2L));
    }

    @Test
    void shouldDeletePersonAndAddress() {
        PersonEntity entity = new PersonEntity(1L, "Maria", "maria@email.com", "52998224725", 5L);
        when(personRepository.findById(1L)).thenReturn(Optional.of(entity));

        gateway.deleteById(1L);

        verify(personRepository).delete(entity);
        verify(addressRepository).deleteById(5L);
    }

    @Test
    void shouldWrapRepositoryFailures() {
        when(personRepository.existsById(1L)).thenThrow(new IllegalStateException("falha"));
        when(personPersistence.findAll()).thenThrow(new IllegalStateException("falha"));

        assertThrows(RepositoryAccessException.class, () -> gateway.existsById(1L));
        assertThrows(RepositoryAccessException.class, () -> gateway.findAll());
    }
}
