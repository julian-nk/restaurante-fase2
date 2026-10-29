package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.UserType;
import br.com.fiap.restaurante.core.factory.impl.UserTypeFactoryImpl;
import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UpdateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.UserTypeAlreadyExistsException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
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

class UserTypeUseCasesTest {

    private final UserTypeFactoryImpl factory = new UserTypeFactoryImpl();
    private UserTypeGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = mock(UserTypeGateway.class);
    }

    private UserType persisted(Long id, String name, String code) {
        return factory.restore(id, name, code);
    }

    @Nested
    class Create {

        private CreateUserTypeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new CreateUserTypeUseCaseImpl(gateway, factory);
        }

        @Test
        void shouldCreateUserType() {
            when(gateway.existsByCode("customer")).thenReturn(false);
            when(gateway.save(any())).thenReturn(persisted(1L, "Cliente", "customer"));

            UserTypeOutputDto output = useCase.execute(new CreateUserTypeInputDto(" Cliente ", " customer "));

            assertEquals(new UserTypeOutputDto(1L, "Cliente", "customer"), output);
            ArgumentCaptor<UserType> captor = ArgumentCaptor.forClass(UserType.class);
            verify(gateway).save(captor.capture());
            assertNull(captor.getValue().getId());
            assertEquals("Cliente", captor.getValue().getName());
        }

        @Test
        void shouldRejectDuplicatedCode() {
            when(gateway.existsByCode("customer")).thenReturn(true);

            assertThrows(UserTypeAlreadyExistsException.class,
                    () -> useCase.execute(new CreateUserTypeInputDto("Cliente", "customer")));

            verify(gateway, never()).save(any());
        }

        @Test
        void shouldRejectInvalidDataBeforeQueryingGateway() {
            assertThrows(InvalidFieldException.class,
                    () -> useCase.execute(new CreateUserTypeInputDto("", "customer")));

            verifyNoInteractions(gateway);
        }
    }

    @Nested
    class GetAll {

        @Test
        void shouldReturnAllUserTypes() {
            when(gateway.findAll()).thenReturn(List.of(
                    persisted(1L, "Cliente", "customer"),
                    persisted(2L, "Dono de Restaurante", "restaurant_owner")));

            List<UserTypeOutputDto> output = new GetUserTypesUseCaseImpl(gateway).execute();

            assertEquals(List.of(
                    new UserTypeOutputDto(1L, "Cliente", "customer"),
                    new UserTypeOutputDto(2L, "Dono de Restaurante", "restaurant_owner")), output);
        }

        @Test
        void shouldReturnEmptyList() {
            when(gateway.findAll()).thenReturn(List.of());

            assertTrue(new GetUserTypesUseCaseImpl(gateway).execute().isEmpty());
        }
    }

    @Nested
    class GetById {

        private GetUserTypeByIdUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetUserTypeByIdUseCaseImpl(gateway);
        }

        @Test
        void shouldReturnUserType() {
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted(1L, "Cliente", "customer")));

            assertEquals(new UserTypeOutputDto(1L, "Cliente", "customer"), useCase.execute(1L));
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UserTypeNotFoundException.class, () -> useCase.execute(99L));
        }
    }

    @Nested
    class Update {

        private UpdateUserTypeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateUserTypeUseCaseImpl(gateway);
            when(gateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        void shouldUpdateUserTypeKeepingSameCode() {
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted(1L, "Cliente", "customer")));

            UserTypeOutputDto output = useCase.execute(new UpdateUserTypeInputDto(1L, "Cliente VIP", "customer"));

            assertEquals(new UserTypeOutputDto(1L, "Cliente VIP", "customer"), output);
            verify(gateway, never()).existsByCode(any());
        }

        @Test
        void shouldUpdateUserTypeWithNewUnusedCode() {
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted(1L, "Cliente", "customer")));
            when(gateway.existsByCode("vip")).thenReturn(false);

            UserTypeOutputDto output = useCase.execute(new UpdateUserTypeInputDto(1L, "Cliente VIP", "vip"));

            assertEquals(new UserTypeOutputDto(1L, "Cliente VIP", "vip"), output);
        }

        @Test
        void shouldRejectCodeUsedByAnotherUserType() {
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted(1L, "Cliente", "customer")));
            when(gateway.existsByCode("restaurant_owner")).thenReturn(true);

            assertThrows(UserTypeAlreadyExistsException.class,
                    () -> useCase.execute(new UpdateUserTypeInputDto(1L, "Dono", "restaurant_owner")));

            verify(gateway, never()).save(any());
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UserTypeNotFoundException.class,
                    () -> useCase.execute(new UpdateUserTypeInputDto(99L, "Cliente", "customer")));
        }

        @Test
        void shouldRejectInvalidData() {
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted(1L, "Cliente", "customer")));

            assertThrows(InvalidFieldException.class,
                    () -> useCase.execute(new UpdateUserTypeInputDto(1L, " ", "customer")));

            verify(gateway, never()).save(any());
        }
    }

    @Nested
    class Delete {

        private DeleteUserTypeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DeleteUserTypeUseCaseImpl(gateway);
        }

        @Test
        void shouldDeleteUserType() {
            when(gateway.findById(1L)).thenReturn(Optional.of(persisted(1L, "Cliente", "customer")));

            useCase.execute(1L);

            verify(gateway).deleteById(1L);
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UserTypeNotFoundException.class, () -> useCase.execute(99L));

            verify(gateway, never()).deleteById(any());
        }
    }
}
