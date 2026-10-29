package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.domain.StatesEnum;
import br.com.fiap.restaurante.core.dto.AddressInputDto;
import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.UpdateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.exception.CustomerAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.CustomerNotFoundException;
import br.com.fiap.restaurante.core.exception.EmailAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerAlreadyRegisteredException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerHasRestaurantsException;
import br.com.fiap.restaurante.core.exception.RestaurantOwnerNotFoundException;
import br.com.fiap.restaurante.core.factory.impl.AddressFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.CustomerFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.RestaurantOwnerFactoryImpl;
import br.com.fiap.restaurante.core.gateway.CustomerGateway;
import br.com.fiap.restaurante.core.gateway.PersonGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantGateway;
import br.com.fiap.restaurante.core.gateway.RestaurantOwnerGateway;
import br.com.fiap.restaurante.core.usecase.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerAndRestaurantOwnerUseCasesTest {

    private static final OffsetDateTime CREATED_ON = OffsetDateTime.parse("2026-01-10T10:00:00-03:00");
    private static final String CPF = "52998224725";
    private static final AddressInputDto ADDRESS = new AddressInputDto(
            "Av. Paulista", "1000", null, "Bela Vista", "São Paulo", StatesEnum.SP, "01310-100");

    private final AddressFactoryImpl addressFactory = new AddressFactoryImpl();
    private final CustomerFactoryImpl customerFactory = new CustomerFactoryImpl(addressFactory);
    private final RestaurantOwnerFactoryImpl ownerFactory = new RestaurantOwnerFactoryImpl(addressFactory);

    private PersonGateway personGateway;
    private CustomerGateway customerGateway;
    private RestaurantOwnerGateway ownerGateway;

    @BeforeEach
    void setUp() {
        personGateway = mock(PersonGateway.class);
        customerGateway = mock(CustomerGateway.class);
        ownerGateway = mock(RestaurantOwnerGateway.class);
        when(customerGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(ownerGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Customer storedCustomer() {
        return customerFactory.restore(1L, "Maria", "maria@email.com", CPF,
                addressFactory.restore(9L, "Rua A", "1", null, "Centro", "Campinas", "SP", "13010000"), CREATED_ON);
    }

    private RestaurantOwner storedOwner() {
        return ownerFactory.restore(1L, "Maria", "maria@email.com", CPF, null, CREATED_ON, "contato@cantina.com");
    }

    @Nested
    class Customers {

        private final CreateCustomerInputDto input = new CreateCustomerInputDto("Maria", "maria@email.com", CPF, ADDRESS);

        private RegisterCustomerUseCaseImpl register() {
            return new RegisterCustomerUseCaseImpl(customerGateway, personGateway, customerFactory);
        }

        @Test
        void shouldRegisterNewPerson() {
            CustomerOutputDto output = register().execute(input);

            ArgumentCaptor<Customer> saved = ArgumentCaptor.forClass(Customer.class);
            verify(customerGateway).save(saved.capture());
            assertNull(saved.getValue().getId());
            assertEquals("Maria", output.name());
            assertEquals("Av. Paulista", output.address().street());
            assertNotNull(output.createdOn());
            verify(personGateway).isEmailUsedByAnotherPerson("maria@email.com", null);
        }

        @Test
        void shouldReusePersonWithSameCpfAndReplaceItsData() {
            when(personGateway.findIdByCpf(CPF)).thenReturn(Optional.of(7L));

            CustomerOutputDto output = register().execute(input);

            assertEquals(7L, output.id());
            assertEquals("Maria", output.name());
            verify(personGateway).isEmailUsedByAnotherPerson("maria@email.com", 7L);
        }

        @Test
        void shouldFindPersonByCpfWrittenWithPunctuation() {
            when(personGateway.findIdByCpf(CPF)).thenReturn(Optional.of(7L));

            CustomerOutputDto output = register().execute(
                    new CreateCustomerInputDto("Maria", "maria@email.com", "529.982.247-25", ADDRESS));

            assertEquals(7L, output.id());
            assertEquals(CPF, output.cpf());
        }

        @Test
        void shouldRejectInvalidCpfBeforeLookingForThePerson() {
            assertThrows(InvalidFieldException.class, () -> register().execute(
                    new CreateCustomerInputDto("Maria", "maria@email.com", "529.982", ADDRESS)));
            verify(personGateway, never()).findIdByCpf(any());
            verify(customerGateway, never()).save(any());
        }

        @Test
        void shouldRejectPersonThatIsAlreadyCustomer() {
            when(personGateway.findIdByCpf(CPF)).thenReturn(Optional.of(7L));
            when(customerGateway.existsById(7L)).thenReturn(true);

            assertThrows(CustomerAlreadyRegisteredException.class, () -> register().execute(input));
            verify(customerGateway, never()).save(any());
        }

        @Test
        void shouldRejectEmailOfAnotherPerson() {
            when(personGateway.isEmailUsedByAnotherPerson("maria@email.com", null)).thenReturn(true);

            assertThrows(EmailAlreadyRegisteredException.class, () -> register().execute(input));
            verify(customerGateway, never()).save(any());
        }

        @Test
        void shouldUpdatePersonalDataKeepingTheAddressRecord() {
            when(customerGateway.findById(1L)).thenReturn(Optional.of(storedCustomer()));
            var useCase = new UpdateCustomerUseCaseImpl(customerGateway, personGateway, addressFactory);

            CustomerOutputDto output = useCase.execute(
                    new UpdateCustomerInputDto(1L, "Maria Souza", "maria.souza@email.com", ADDRESS));

            assertEquals("Maria Souza", output.name());
            assertEquals(9L, output.address().id());
            assertEquals("Av. Paulista", output.address().street());
            assertEquals(CPF, output.cpf());
        }

        @Test
        void shouldRemoveAddressWhenUpdatingWithoutOne() {
            when(customerGateway.findById(1L)).thenReturn(Optional.of(storedCustomer()));
            var useCase = new UpdateCustomerUseCaseImpl(customerGateway, personGateway, addressFactory);

            assertNull(useCase.execute(new UpdateCustomerInputDto(1L, "Maria", "maria@email.com", null)).address());
        }

        @Test
        void shouldNotUpdateUnknownCustomerOrUseEmailOfAnotherPerson() {
            var useCase = new UpdateCustomerUseCaseImpl(customerGateway, personGateway, addressFactory);
            when(customerGateway.findById(1L)).thenReturn(Optional.of(storedCustomer()));
            when(personGateway.isEmailUsedByAnotherPerson("joao@email.com", 1L)).thenReturn(true);

            assertThrows(CustomerNotFoundException.class,
                    () -> useCase.execute(new UpdateCustomerInputDto(2L, "Maria", "maria@email.com", null)));
            assertThrows(EmailAlreadyRegisteredException.class,
                    () -> useCase.execute(new UpdateCustomerInputDto(1L, "Maria", "joao@email.com", null)));
            verify(customerGateway, never()).save(any());
        }

        @Test
        void shouldDeleteOnlyTheRoleWhenPersonIsAlsoOwner() {
            when(customerGateway.existsById(1L)).thenReturn(true);
            when(ownerGateway.existsById(1L)).thenReturn(true);

            new DeleteCustomerUseCaseImpl(customerGateway, ownerGateway, personGateway).execute(1L);

            verify(customerGateway).deleteById(1L);
            verify(personGateway, never()).deleteById(any());
        }

        @Test
        void shouldDeletePersonWhenCustomerWasItsOnlyRole() {
            when(customerGateway.existsById(1L)).thenReturn(true);

            new DeleteCustomerUseCaseImpl(customerGateway, ownerGateway, personGateway).execute(1L);

            verify(personGateway).deleteById(1L);
            verify(customerGateway, never()).deleteById(any());
        }

        @Test
        void shouldNotDeleteUnknownCustomer() {
            var useCase = new DeleteCustomerUseCaseImpl(customerGateway, ownerGateway, personGateway);

            assertThrows(CustomerNotFoundException.class, () -> useCase.execute(1L));
        }

        @Test
        void shouldListAndFind() {
            when(customerGateway.findAll()).thenReturn(List.of(storedCustomer()));
            when(customerGateway.findById(1L)).thenReturn(Optional.of(storedCustomer()));

            assertEquals(1, new GetCustomersUseCaseImpl(customerGateway).execute().size());
            assertEquals("Maria", new GetCustomerByIdUseCaseImpl(customerGateway).execute(1L).name());
            assertThrows(CustomerNotFoundException.class, () -> new GetCustomerByIdUseCaseImpl(customerGateway).execute(2L));
        }
    }

    @Nested
    class RestaurantOwners {

        private final CreateRestaurantOwnerInputDto input = new CreateRestaurantOwnerInputDto(
                "Maria", "maria@email.com", CPF, null, "contato@cantina.com");
        private RestaurantGateway restaurantGateway;

        @BeforeEach
        void setUp() {
            restaurantGateway = mock(RestaurantGateway.class);
        }

        private RegisterRestaurantOwnerUseCaseImpl register() {
            return new RegisterRestaurantOwnerUseCaseImpl(ownerGateway, personGateway, ownerFactory);
        }

        private DeleteRestaurantOwnerUseCaseImpl delete() {
            return new DeleteRestaurantOwnerUseCaseImpl(ownerGateway, restaurantGateway, customerGateway, personGateway);
        }

        @Test
        void shouldRegisterNewPerson() {
            RestaurantOwnerOutputDto output = register().execute(input);

            assertNull(output.id());
            assertEquals("contato@cantina.com", output.businessEmail());
            assertNotNull(output.createdOn());
        }

        @Test
        void shouldReusePersonWithSameCpfAndReplaceItsData() {
            when(personGateway.findIdByCpf(CPF)).thenReturn(Optional.of(7L));

            assertEquals(7L, register().execute(input).id());
            verify(personGateway).isEmailUsedByAnotherPerson("maria@email.com", 7L);
        }

        @Test
        void shouldFindPersonByCpfWrittenWithPunctuation() {
            when(personGateway.findIdByCpf(CPF)).thenReturn(Optional.of(7L));

            assertEquals(7L, register().execute(new CreateRestaurantOwnerInputDto(
                    "Maria", "maria@email.com", "529.982.247-25", null, "contato@cantina.com")).id());
        }

        @Test
        void shouldRejectPersonThatIsAlreadyOwnerOrEmailOfAnotherPerson() {
            when(personGateway.findIdByCpf(CPF)).thenReturn(Optional.of(7L));
            when(ownerGateway.existsById(7L)).thenReturn(true);
            assertThrows(RestaurantOwnerAlreadyRegisteredException.class, () -> register().execute(input));

            when(ownerGateway.existsById(7L)).thenReturn(false);
            when(personGateway.isEmailUsedByAnotherPerson("maria@email.com", 7L)).thenReturn(true);
            assertThrows(EmailAlreadyRegisteredException.class, () -> register().execute(input));

            verify(ownerGateway, never()).save(any());
        }

        @Test
        void shouldUpdatePersonalDataAndBusinessEmail() {
            when(ownerGateway.findById(1L)).thenReturn(Optional.of(storedOwner()));
            var useCase = new UpdateRestaurantOwnerUseCaseImpl(ownerGateway, personGateway, addressFactory);

            RestaurantOwnerOutputDto output = useCase.execute(new UpdateRestaurantOwnerInputDto(
                    1L, "Maria Souza", "maria.souza@email.com", ADDRESS, "novo@cantina.com"));

            assertEquals("Maria Souza", output.name());
            assertEquals("Av. Paulista", output.address().street());
            assertEquals("novo@cantina.com", output.businessEmail());
        }

        @Test
        void shouldNotUpdateUnknownOwnerOrUseEmailOfAnotherPerson() {
            var useCase = new UpdateRestaurantOwnerUseCaseImpl(ownerGateway, personGateway, addressFactory);
            when(ownerGateway.findById(1L)).thenReturn(Optional.of(storedOwner()));
            when(personGateway.isEmailUsedByAnotherPerson("joao@email.com", 1L)).thenReturn(true);

            assertThrows(RestaurantOwnerNotFoundException.class, () -> useCase.execute(new UpdateRestaurantOwnerInputDto(
                    2L, "Maria", "maria@email.com", null, "contato@cantina.com")));
            assertThrows(EmailAlreadyRegisteredException.class, () -> useCase.execute(new UpdateRestaurantOwnerInputDto(
                    1L, "Maria", "joao@email.com", null, "contato@cantina.com")));
            verify(ownerGateway, never()).save(any());
        }

        @Test
        void shouldDeleteOnlyTheRoleWhenPersonIsAlsoCustomer() {
            when(ownerGateway.existsById(1L)).thenReturn(true);
            when(customerGateway.existsById(1L)).thenReturn(true);

            delete().execute(1L);

            verify(ownerGateway).deleteById(1L);
            verify(personGateway, never()).deleteById(any());
        }

        @Test
        void shouldDeletePersonWhenOwnerWasItsOnlyRole() {
            when(ownerGateway.existsById(1L)).thenReturn(true);

            delete().execute(1L);

            verify(personGateway).deleteById(1L);
            verify(ownerGateway, never()).deleteById(any());
        }

        @Test
        void shouldNotDeleteUnknownOwnerOrOwnerWithRestaurants() {
            assertThrows(RestaurantOwnerNotFoundException.class, () -> delete().execute(1L));

            when(ownerGateway.existsById(1L)).thenReturn(true);
            when(restaurantGateway.existsByOwnerId(1L)).thenReturn(true);
            assertThrows(RestaurantOwnerHasRestaurantsException.class, () -> delete().execute(1L));

            verify(personGateway, never()).deleteById(any());
            verify(ownerGateway, never()).deleteById(any());
        }

        @Test
        void shouldListAndFind() {
            when(ownerGateway.findAll()).thenReturn(List.of(storedOwner()));
            when(ownerGateway.findById(1L)).thenReturn(Optional.of(storedOwner()));

            assertEquals(1, new GetRestaurantOwnersUseCaseImpl(ownerGateway).execute().size());
            assertEquals(CREATED_ON, new GetRestaurantOwnerByIdUseCaseImpl(ownerGateway).execute(1L).createdOn());
            assertThrows(RestaurantOwnerNotFoundException.class,
                    () -> new GetRestaurantOwnerByIdUseCaseImpl(ownerGateway).execute(2L));
        }
    }
}
