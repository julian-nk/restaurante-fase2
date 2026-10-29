package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.factory.impl.UserTypeFactoryImpl;
import br.com.fiap.restaurante.core.dto.AppUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.AppUserTypeGateway;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.impl.AssociateUserTypeUseCaseImpl;
import br.com.fiap.restaurante.core.usecase.impl.GetUserTypesByAppUserUseCaseImpl;
import br.com.fiap.restaurante.core.usecase.impl.RemoveUserTypeFromAppUserUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AppUserTypeUseCasesTest {

    private final UserTypeFactoryImpl userTypeFactory = new UserTypeFactoryImpl();
    private AppUserTypeGateway appUserTypeGateway;
    private AppUserGateway appUserGateway;
    private UserTypeGateway userTypeGateway;

    @BeforeEach
    void setUp() {
        appUserTypeGateway = mock(AppUserTypeGateway.class);
        appUserGateway = mock(AppUserGateway.class);
        userTypeGateway = mock(UserTypeGateway.class);
    }

    @Nested
    class Associate {

        private AssociateUserTypeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new AssociateUserTypeUseCaseImpl(appUserTypeGateway, appUserGateway, userTypeGateway);
        }

        @Test
        void shouldAssociateUserType() {
            when(appUserGateway.existsById(1L)).thenReturn(true);
            when(userTypeGateway.findById(2L)).thenReturn(Optional.of(userTypeFactory.restore(2L, "Cliente", "customer")));

            useCase.execute(new AppUserTypeInputDto(1L, 2L));

            verify(appUserTypeGateway).associate(1L, 2L);
        }

        @Test
        void shouldThrowWhenAppUserDoesNotExist() {
            when(appUserGateway.existsById(1L)).thenReturn(false);

            assertThrows(AppUserNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));

            verify(appUserTypeGateway, never()).associate(anyLong(), anyLong());
        }

        @Test
        void shouldThrowWhenUserTypeDoesNotExist() {
            when(appUserGateway.existsById(1L)).thenReturn(true);
            when(userTypeGateway.findById(2L)).thenReturn(Optional.empty());

            assertThrows(UserTypeNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));

            verify(appUserTypeGateway, never()).associate(anyLong(), anyLong());
        }
    }

    @Nested
    class GetByAppUser {

        private GetUserTypesByAppUserUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetUserTypesByAppUserUseCaseImpl(appUserTypeGateway, appUserGateway);
        }

        @Test
        void shouldReturnUserTypesOfAppUser() {
            when(appUserGateway.existsById(1L)).thenReturn(true);
            when(appUserTypeGateway.findUserTypesByAppUserId(1L))
                    .thenReturn(List.of(userTypeFactory.restore(2L, "Cliente", "customer")));

            assertEquals(List.of(new UserTypeOutputDto(2L, "Cliente", "customer")), useCase.execute(1L));
        }

        @Test
        void shouldThrowWhenAppUserDoesNotExist() {
            when(appUserGateway.existsById(1L)).thenReturn(false);

            assertThrows(AppUserNotFoundException.class, () -> useCase.execute(1L));
        }
    }

    @Nested
    class Remove {

        private RemoveUserTypeFromAppUserUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new RemoveUserTypeFromAppUserUseCaseImpl(appUserTypeGateway, appUserGateway, userTypeGateway);
        }

        @Test
        void shouldRemoveUserType() {
            when(appUserGateway.existsById(1L)).thenReturn(true);
            when(userTypeGateway.findById(2L)).thenReturn(Optional.of(userTypeFactory.restore(2L, "Cliente", "customer")));

            useCase.execute(new AppUserTypeInputDto(1L, 2L));

            verify(appUserTypeGateway).remove(1L, 2L);
        }

        @Test
        void shouldThrowWhenAppUserDoesNotExist() {
            when(appUserGateway.existsById(1L)).thenReturn(false);

            assertThrows(AppUserNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));
        }

        @Test
        void shouldThrowWhenUserTypeDoesNotExist() {
            when(appUserGateway.existsById(1L)).thenReturn(true);
            when(userTypeGateway.findById(2L)).thenReturn(Optional.empty());

            assertThrows(UserTypeNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));

            verify(appUserTypeGateway, never()).remove(anyLong(), anyLong());
        }
    }
}
