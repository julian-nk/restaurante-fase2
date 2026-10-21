
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private DeleteUserTypeUseCase deleteUserTypeUseCase;

    @BeforeEach
    void setUp() {
        deleteUserTypeUseCase =
                new DeleteUserTypeUseCase(userTypeGateway);
    }

    @Test
    void shouldDeleteUserTypeWhenIdExists() {
        // Arrange
        Long id = 1L;
        UserType userType = new UserType("Cliente", "customer");

        when(userTypeGateway.findById(id))
                .thenReturn(Optional.of(userType));

        // Act
        boolean result = deleteUserTypeUseCase.execute(id);

        // Assert
        assertTrue(result);

        verify(userTypeGateway).findById(id);
        verify(userTypeGateway).deleteById(id);
    }

    @Test
    void shouldReturnFalseWhenIdDoesNotExist() {
        // Arrange
        Long id = 999L;

        when(userTypeGateway.findById(id))
                .thenReturn(Optional.empty());

        // Act
        boolean result = deleteUserTypeUseCase.execute(id);

        // Assert
        assertFalse(result);

        verify(userTypeGateway).findById(id);
        verify(userTypeGateway, never()).deleteById(anyLong());
    }
}
