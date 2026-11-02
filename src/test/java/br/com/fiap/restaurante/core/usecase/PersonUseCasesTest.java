package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.Person;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.CreatePersonInputDto;
import br.com.fiap.restaurante.core.dto.PersonOutputDto;
import br.com.fiap.restaurante.core.dto.UpdatePersonInputDto;
import br.com.fiap.restaurante.core.exception.CpfAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.PersonNotFoundException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerHasRestaurantsException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.PersonFactoryImpl;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.usecase.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonUseCasesTest {

    private static final String CPF = "39053344705";
    private static final AddressInputDto ADDRESS = new AddressInputDto(
            "Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310-100");

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final PersonFactoryImpl personFactory = new PersonFactoryImpl(addressFactory);

    private PersonGateway personGateway;
    private RestaurantGateway restaurantGateway;

    @BeforeEach
    void setUp() {
        personGateway = mock(PersonGateway.class);
        restaurantGateway = mock(RestaurantGateway.class);
        when(personGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Person storedPerson() {
        return personFactory.restore(1L, "Ana", "ana@email.com", CPF,
                addressFactory.restore(9L, "Rua A", "1", null, "Centro", "Campinas", "SP", "13010000"));
    }

    @Nested
    class Create {

        private final CreatePersonInputDto input = new CreatePersonInputDto("Ana", "ana@email.com", CPF, ADDRESS);

        private CreatePersonUseCaseImpl create() {
            return new CreatePersonUseCaseImpl(personGateway, personFactory);
        }

        @Test
        void shouldCreatePersonWithoutRole() {
            PersonOutputDto output = create().execute(input);

            ArgumentCaptor<Person> saved = ArgumentCaptor.forClass(Person.class);
            verify(personGateway).save(saved.capture());
            assertNull(saved.getValue().getId());
            assertEquals(Person.class, saved.getValue().getClass());
            assertEquals("Ana", output.name());
            assertEquals("Av. Paulista", output.address().street());
            verify(personGateway).isEmailUsedByAnotherPerson("ana@email.com", null);
        }

        @Test
        void shouldLookForTheCpfWithoutPunctuation() {
            PersonOutputDto output = create().execute(
                    new CreatePersonInputDto("Ana", "ana@email.com", "390.533.447-05", null));

            assertEquals(CPF, output.cpf());
            assertNull(output.address());
            verify(personGateway).findIdByCpf(CPF);
        }

        @Test
        void shouldRejectCpfOfAnotherPerson() {
            when(personGateway.findIdByCpf(CPF)).thenReturn(Optional.of(7L));

            assertThrows(CpfAlreadyRegisteredException.class, () -> create().execute(input));
            verify(personGateway, never()).save(any());
        }

        @Test
        void shouldRejectEmailOfAnotherPerson() {
            when(personGateway.isEmailUsedByAnotherPerson("ana@email.com", null)).thenReturn(true);

            assertThrows(EmailAlreadyRegisteredException.class, () -> create().execute(input));
            verify(personGateway, never()).save(any());
        }

        @Test
        void shouldRejectInvalidCpfBeforeLookingForThePerson() {
            assertThrows(InvalidFieldException.class, () -> create().execute(
                    new CreatePersonInputDto("Ana", "ana@email.com", "390.533", null)));
            verify(personGateway, never()).findIdByCpf(any());
            verify(personGateway, never()).save(any());
        }
    }

    @Nested
    class Update {

        private UpdatePersonUseCaseImpl update() {
            return new UpdatePersonUseCaseImpl(personGateway, addressFactory);
        }

        @Test
        void shouldUpdatePersonalDataKeepingTheAddressRecord() {
            when(personGateway.findById(1L)).thenReturn(Optional.of(storedPerson()));

            PersonOutputDto output = update().execute(
                    new UpdatePersonInputDto(1L, "Ana Pereira", "ana.pereira@email.com", ADDRESS));

            assertEquals("Ana Pereira", output.name());
            assertEquals("ana.pereira@email.com", output.email());
            assertEquals(9L, output.address().id());
            assertEquals("Av. Paulista", output.address().street());
            assertEquals(CPF, output.cpf());
        }

        @Test
        void shouldRemoveAddressWhenUpdatingWithoutOne() {
            when(personGateway.findById(1L)).thenReturn(Optional.of(storedPerson()));

            assertNull(update().execute(new UpdatePersonInputDto(1L, "Ana", "ana@email.com", null)).address());
        }

        @Test
        void shouldNotUpdateUnknownPersonOrUseEmailOfAnotherPerson() {
            when(personGateway.findById(1L)).thenReturn(Optional.of(storedPerson()));
            when(personGateway.isEmailUsedByAnotherPerson("joao@email.com", 1L)).thenReturn(true);

            assertThrows(PersonNotFoundException.class,
                    () -> update().execute(new UpdatePersonInputDto(2L, "Ana", "ana@email.com", null)));
            assertThrows(EmailAlreadyRegisteredException.class,
                    () -> update().execute(new UpdatePersonInputDto(1L, "Ana", "joao@email.com", null)));
            verify(personGateway, never()).save(any());
        }
    }

    @Nested
    class Delete {

        private DeletePersonUseCaseImpl delete() {
            return new DeletePersonUseCaseImpl(personGateway, restaurantGateway);
        }

        @Test
        void shouldDeletePerson() {
            when(personGateway.existsById(1L)).thenReturn(true);

            delete().execute(1L);

            verify(personGateway).deleteById(1L);
        }

        @Test
        void shouldNotDeleteUnknownPerson() {
            assertThrows(PersonNotFoundException.class, () -> delete().execute(1L));
            verify(personGateway, never()).deleteById(any());
        }

        @Test
        void shouldNotDeletePersonThatStillOwnsRestaurants() {
            when(personGateway.existsById(1L)).thenReturn(true);
            when(restaurantGateway.existsByOwnerId(1L)).thenReturn(true);

            assertThrows(RestaurantOwnerHasRestaurantsException.class, () -> delete().execute(1L));
            verify(personGateway, never()).deleteById(any());
        }
    }

    @Test
    void shouldListAndFind() {
        when(personGateway.findAll()).thenReturn(List.of(storedPerson()));
        when(personGateway.findById(1L)).thenReturn(Optional.of(storedPerson()));

        assertEquals(1, new GetPersonsUseCaseImpl(personGateway).execute().size());
        assertEquals("Ana", new GetPersonByIdUseCaseImpl(personGateway).execute(1L).name());
        assertThrows(PersonNotFoundException.class, () -> new GetPersonByIdUseCaseImpl(personGateway).execute(2L));
    }
}
