
package br.com.fiap.restaurante.domain.usecases;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserTypesUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private GetUserTypesUseCase getUserTypesUseCase;

    @BeforeEach
    void setUp() {
        getUserTypesUseCase = new GetUserTypesUseCase(userTypeGateway);
    }

    @Test
    void shouldReturnAllUserTypes() {
        // Arrange
        List<UserType> userTypes = List.of(
                new UserType("Cliente", "customer"),
                new UserType("Funcionário", "employee")
        );

        when(userTypeGateway.findAll()).thenReturn(userTypes);

        // Act
        List<UserType> result = getUserTypesUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cliente", result.get(0).getName());
        assertEquals("customer", result.get(0).getCode());
        assertEquals("Funcionário", result.get(1).getName());
        assertEquals("employee", result.get(1).getCode());

        verify(userTypeGateway).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoUserTypesExist() {
        // Arrange
        when(userTypeGateway.findAll()).thenReturn(List.of());

        // Act
        List<UserType> result = getUserTypesUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userTypeGateway).findAll();
    }
}
