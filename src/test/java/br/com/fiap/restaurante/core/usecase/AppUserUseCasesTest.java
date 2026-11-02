package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.domain.AppUser;
import br.com.fiap.restaurante.core.factory.impl.AppUserFactoryImpl;
import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.core.dto.ChangePasswordInputDto;
import br.com.fiap.restaurante.core.dto.RequesterDto;
import br.com.fiap.restaurante.core.exception.AppUserNotFoundException;
import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import br.com.fiap.restaurante.core.exception.InvalidPasswordException;
import br.com.fiap.restaurante.core.exception.PasswordChangeNotAllowedException;
import br.com.fiap.restaurante.core.gateway.AppUserGateway;
import br.com.fiap.restaurante.core.gateway.PasswordEncoderGateway;
import br.com.fiap.restaurante.core.usecase.impl.*;
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

class AppUserUseCasesTest {

    private final AppUserFactoryImpl factory = new AppUserFactoryImpl();
    private AppUserGateway gateway;
    private PasswordEncoderGateway passwordEncoder;

    @BeforeEach
    void setUp() {
        gateway = mock(AppUserGateway.class);
        passwordEncoder = mock(PasswordEncoderGateway.class);
        when(passwordEncoder.encode(any())).thenAnswer(invocation -> "hash:" + invocation.getArgument(0));
        when(gateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private AppUser maria() {
        return factory.restore(1L, 10L, "maria", "hash:senhaAtual1", Set.of(2L));
    }

    @Nested
    class Queries {

        @Test
        void shouldListAndFindUsers() {
            when(gateway.findAll()).thenReturn(List.of(maria()));
            when(gateway.findById(1L)).thenReturn(Optional.of(maria()));

            assertEquals(List.of(new AppUserOutputDto(1L, 10L, "maria")), new GetAppUsersUseCaseImpl(gateway).execute());
            assertEquals("maria", new GetAppUserByIdUseCaseImpl(gateway).execute(1L).login());
        }

        @Test
        void shouldThrowWhenNotFound() {
            when(gateway.findById(9L)).thenReturn(Optional.empty());

            assertThrows(AppUserNotFoundException.class, () -> new GetAppUserByIdUseCaseImpl(gateway).execute(9L));
        }
    }

    @Nested
    class ChangePassword {

        /** A Maria (usuário 1) é a pessoa 10. */
        private static final RequesterDto MARIA = new RequesterDto(10L, false);

        private ChangeAppUserPasswordUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ChangeAppUserPasswordUseCaseImpl(gateway, passwordEncoder);
            when(gateway.findById(1L)).thenReturn(Optional.of(maria()));
        }

        @Test
        void shouldChangePassword() {
            when(passwordEncoder.matches("senhaAtual1", "hash:senhaAtual1")).thenReturn(true);

            useCase.execute(new ChangePasswordInputDto(1L, "senhaAtual1", "senhaNova2"), MARIA);

            ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
            verify(gateway).save(captor.capture());
            assertEquals("hash:senhaNova2", captor.getValue().getPasswordHash());
        }

        @Test
        void shouldRejectWrongCurrentPassword() {
            when(passwordEncoder.matches("errada123", "hash:senhaAtual1")).thenReturn(false);

            assertThrows(InvalidPasswordException.class,
                    () -> useCase.execute(new ChangePasswordInputDto(1L, "errada123", "senhaNova2"), MARIA));
            assertThrows(InvalidPasswordException.class,
                    () -> useCase.execute(new ChangePasswordInputDto(1L, null, "senhaNova2"), MARIA));
            verify(gateway, never()).save(any());
        }

        @Test
        void shouldRejectWeakNewPassword() {
            when(passwordEncoder.matches("senhaAtual1", "hash:senhaAtual1")).thenReturn(true);

            assertThrows(InvalidFieldException.class,
                    () -> useCase.execute(new ChangePasswordInputDto(1L, "senhaAtual1", "fraca"), MARIA));
        }

        @Test
        void shouldNotChangeThePasswordOfAnotherUser() {
            when(passwordEncoder.matches("senhaAtual1", "hash:senhaAtual1")).thenReturn(true);

            // nem sabendo a senha atual, nem sendo administrador
            assertThrows(PasswordChangeNotAllowedException.class, () -> useCase.execute(
                    new ChangePasswordInputDto(1L, "senhaAtual1", "senhaNova2"), new RequesterDto(11L, false)));
            assertThrows(PasswordChangeNotAllowedException.class, () -> useCase.execute(
                    new ChangePasswordInputDto(1L, "senhaAtual1", "senhaNova2"), new RequesterDto(99L, true)));
            verify(gateway, never()).save(any());
        }

        @Test
        void shouldAnswerTheSameForUnknownUser() {
            assertThrows(PasswordChangeNotAllowedException.class,
                    () -> useCase.execute(new ChangePasswordInputDto(9L, "senhaAtual1", "senhaNova2"), MARIA));
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteUser() {
            when(gateway.existsById(1L)).thenReturn(true);

            new DeleteAppUserUseCaseImpl(gateway).execute(1L);

            verify(gateway).deleteById(1L);
        }

        @Test
        void shouldThrowWhenNotFound() {
            assertThrows(AppUserNotFoundException.class, () -> new DeleteAppUserUseCaseImpl(gateway).execute(9L));
        }
    }
}
