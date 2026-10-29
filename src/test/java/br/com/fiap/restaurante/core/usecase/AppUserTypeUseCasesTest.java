package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.dto.AppUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.core.factory.impl.UserTypeFactoryImpl;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.UserTypeGateway;
import br.com.fiap.restaurante.core.usecase.impl.AssociateUserTypeUseCaseImpl;
import br.com.fiap.restaurante.core.usecase.impl.GetUserTypesByAppUserUseCaseImpl;
import br.com.fiap.restaurante.core.usecase.impl.RemoveUserTypeFromAppUserUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppUserTypeUseCasesTest {

    private final UserTypeFactoryImpl userTypeFactory = new UserTypeFactoryImpl();
    private final AppUserFactoryImpl appUserFactory = new AppUserFactoryImpl();
    private AppUserGateway appUserGateway;
    private UserTypeGateway userTypeGateway;

    @BeforeEach
    void setUp() {
        appUserGateway = mock(AppUserGateway.class);
        userTypeGateway = mock(UserTypeGateway.class);
        when(appUserGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    /** Usuário 1, que já tem o tipo 3. */
    private void storedUserWithType3() {
        when(appUserGateway.findById(1L))
                .thenReturn(Optional.of(appUserFactory.restore(1L, 10L, "maria", "hash", Set.of(3L))));
    }

    private void storedUserType(Long id) {
        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userTypeFactory.restore(id, "Tipo " + id, "tipo_" + id)));
    }

    private Set<Long> savedUserTypeIds() {
        ArgumentCaptor<AppUser> saved = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserGateway).save(saved.capture());
        return saved.getValue().getUserTypeIds();
    }

    @Nested
    class Associate {

        private AssociateUserTypeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new AssociateUserTypeUseCaseImpl(appUserGateway, userTypeGateway);
        }

        @Test
        void shouldAddTheTypeAndSaveTheUser() {
            storedUserWithType3();
            storedUserType(2L);

            useCase.execute(new AppUserTypeInputDto(1L, 2L));

            assertEquals(Set.of(2L, 3L), savedUserTypeIds());
        }

        @Test
        void shouldNotSaveWhenTheUserAlreadyHasTheType() {
            storedUserWithType3();
            storedUserType(3L);

            useCase.execute(new AppUserTypeInputDto(1L, 3L));

            verify(appUserGateway, never()).save(any());
        }

        @Test
        void shouldThrowWhenAppUserDoesNotExist() {
            assertThrows(AppUserNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));

            verify(appUserGateway, never()).save(any());
        }

        @Test
        void shouldThrowWhenUserTypeDoesNotExist() {
            storedUserWithType3();

            assertThrows(UserTypeNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));

            verify(appUserGateway, never()).save(any());
        }
    }

    @Nested
    class GetByAppUser {

        private GetUserTypesByAppUserUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetUserTypesByAppUserUseCaseImpl(appUserGateway, userTypeGateway);
        }

        @Test
        void shouldReturnTheTypesOfTheUser() {
            storedUserWithType3();
            when(userTypeGateway.findAllById(Set.of(3L)))
                    .thenReturn(List.of(userTypeFactory.restore(3L, "Administrador", "admin")));

            assertEquals(List.of(new UserTypeOutputDto(3L, "Administrador", "admin")), useCase.execute(1L));
        }

        @Test
        void shouldReturnEmptyListWithoutQueryingWhenTheUserHasNoTypes() {
            when(appUserGateway.findById(1L))
                    .thenReturn(Optional.of(appUserFactory.restore(1L, 10L, "maria", "hash", Set.of())));

            assertTrue(useCase.execute(1L).isEmpty());
            verify(userTypeGateway, never()).findAllById(any());
        }

        @Test
        void shouldThrowWhenAppUserDoesNotExist() {
            assertThrows(AppUserNotFoundException.class, () -> useCase.execute(1L));
        }
    }

    @Nested
    class Remove {

        private RemoveUserTypeFromAppUserUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new RemoveUserTypeFromAppUserUseCaseImpl(appUserGateway, userTypeGateway);
        }

        @Test
        void shouldRemoveTheTypeAndSaveTheUser() {
            storedUserWithType3();
            storedUserType(3L);

            useCase.execute(new AppUserTypeInputDto(1L, 3L));

            assertTrue(savedUserTypeIds().isEmpty());
        }

        @Test
        void shouldNotSaveWhenTheUserDoesNotHaveTheType() {
            storedUserWithType3();
            storedUserType(2L);

            useCase.execute(new AppUserTypeInputDto(1L, 2L));

            verify(appUserGateway, never()).save(any());
        }

        @Test
        void shouldThrowWhenAppUserDoesNotExist() {
            assertThrows(AppUserNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));
        }

        @Test
        void shouldThrowWhenUserTypeDoesNotExist() {
            storedUserWithType3();

            assertThrows(UserTypeNotFoundException.class, () -> useCase.execute(new AppUserTypeInputDto(1L, 2L)));

            verify(appUserGateway, never()).save(any());
        }
    }
}
