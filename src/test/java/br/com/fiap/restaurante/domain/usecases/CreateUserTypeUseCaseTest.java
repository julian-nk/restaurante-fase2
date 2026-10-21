package br.com.fiap.restaurante.domain.usecases;
import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway gateway;

    private CreateUserTypeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateUserTypeUseCase(gateway);
    }

    @Test
    void shouldCreateUserTypeSuccessfully() {
        UserType savedUserType = new UserType(
                "Funcionário",
                "employee"
        );

        when(gateway.existsByCode("employee")).thenReturn(false);
        when(gateway.save(any(UserType.class))).thenReturn(savedUserType);

        UserType result = useCase.execute("Funcionário", "employee");

        assertNotNull(result);
        assertEquals("Funcionário", result.getName());
        assertEquals("employee", result.getCode());

        verify(gateway).existsByCode("employee");
        verify(gateway).save(any(UserType.class));
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null, "employee")
        );

        assertEquals(
                "O nome do tipo de usuário é obrigatório.",
                exception.getMessage()
        );

        verifyNoInteractions(gateway);
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute("   ", "employee")
        );

        verifyNoInteractions(gateway);
    }

    @Test
    void shouldThrowExceptionWhenCodeIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute("Funcionário", null)
        );

        verifyNoInteractions(gateway);
    }

    @Test
    void shouldThrowExceptionWhenCodeIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute("Funcionário", "   ")
        );

        verifyNoInteractions(gateway);
    }

    @Test
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        when(gateway.existsByCode("employee")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute("Funcionário", "employee")
        );

        assertEquals(
                "Já existe um tipo de usuário com esse código.",
                exception.getMessage()
        );

        verify(gateway).existsByCode("employee");
        verify(gateway, never()).save(any(UserType.class));
    }
}