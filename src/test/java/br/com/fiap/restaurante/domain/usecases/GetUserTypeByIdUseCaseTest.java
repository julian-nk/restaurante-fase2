
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserTypeByIdUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private GetUserTypeByIdUseCase getUserTypeByIdUseCase;

    @BeforeEach
    void setUp() {
        getUserTypeByIdUseCase =
                new GetUserTypeByIdUseCase(userTypeGateway);
    }

    @Test
    void shouldReturnUserTypeWhenIdExists() {
        // Arrange
        Long id = 1L;
        UserType userType = new UserType("Cliente", "customer");

        when(userTypeGateway.findById(id))
                .thenReturn(Optional.of(userType));

        // Act
        Optional<UserType> result =
                getUserTypeByIdUseCase.execute(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Cliente", result.get().getName());
        assertEquals("customer", result.get().getCode());

        verify(userTypeGateway).findById(id);
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        // Arrange
        Long id = 999L;

        when(userTypeGateway.findById(id))
                .thenReturn(Optional.empty());

        // Act
        Optional<UserType> result =
                getUserTypeByIdUseCase.execute(id);

        // Assert
        assertTrue(result.isEmpty());

        verify(userTypeGateway).findById(id);
    }
}
