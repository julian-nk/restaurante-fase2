package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.repository.AddressRepository;
import br.com.fiap.restaurante.infra.database.jpa.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonJpaGatewayTest {

    private PersonRepository personRepository;
    private AddressRepository addressRepository;
    private PersonJpaGateway gateway;

    @BeforeEach
    void setUp() {
        personRepository = mock(PersonRepository.class);
        addressRepository = mock(AddressRepository.class);
        gateway = new PersonJpaGateway(personRepository, addressRepository);
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

        assertThrows(RepositoryAccessException.class, () -> gateway.existsById(1L));
    }
}
