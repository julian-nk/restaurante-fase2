
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private UpdateUserTypeUseCase updateUserTypeUseCase;

    @BeforeEach
    void setUp() {
        updateUserTypeUseCase =
                new UpdateUserTypeUseCase(userTypeGateway);
    }

    @Test
    void shouldUpdateUserTypeWhenIdExists() {
        // Arrange
        Long id = 1L;
        UserType userType = new UserType("Cliente", "customer");

        UserType updatedUserType =
                new UserType("Cliente Premium", "premium_customer");

        when(userTypeGateway.findById(id))
                .thenReturn(Optional.of(userType));

        when(userTypeGateway.save(any(UserType.class)))
                .thenReturn(updatedUserType);

        // Act
        Optional<UserType> result = updateUserTypeUseCase.execute(
                id,
                "Cliente Premium",
                "premium_customer"
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Cliente Premium", result.get().getName());
        assertEquals("premium_customer", result.get().getCode());

        verify(userTypeGateway).findById(id);
        verify(userTypeGateway).save(userType);
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        // Arrange
        Long id = 999L;

        when(userTypeGateway.findById(id))
                .thenReturn(Optional.empty());

        // Act
        Optional<UserType> result = updateUserTypeUseCase.execute(
                id,
                "Cliente Premium",
                "premium_customer"
        );

        // Assert
        assertTrue(result.isEmpty());

        verify(userTypeGateway).findById(id);
        verify(userTypeGateway, never()).save(any(UserType.class));
    }
}

