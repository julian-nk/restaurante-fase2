package br.com.fiap.restaurante.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemBaseExceptionTest {

    @Test
    void shouldExposeCodeStatusAndMessage() {
        assertException(new InvalidFieldException("Campo inválido."),
                "validation.invalidField", 400, "Campo inválido.");
        assertException(new UserTypeNotFoundException(1L),
                "userType.notFound", 404, "Tipo de usuário não encontrado com o ID: 1");
        assertException(new UserTypeAlreadyExistsException("customer"),
                "userType.alreadyExists", 409, "Já existe um tipo de usuário com o código: customer");
        assertException(new MenuItemNotFoundException(2L),
                "menuItem.notFound", 404, "Prato não encontrado com o ID: 2");
        assertException(new AppUserNotFoundException(3L),
                "appUser.notFound", 404, "Usuário não encontrado com o ID: 3");
    }

    @Test
    void shouldKeepCauseOnRepositoryAccessException() {
        RuntimeException cause = new IllegalStateException("conexão perdida");

        RepositoryAccessException exception = new RepositoryAccessException(cause);

        assertException(exception, "repository.accessError", 500, "Erro ao acessar o repositório de dados.");
        assertSame(cause, exception.getCause());
    }

    private static void assertException(SystemBaseException exception, String code, int status, String message) {
        assertEquals(code, exception.getCode());
        assertEquals(status, exception.getHttpStatus());
        assertEquals(message, exception.getMessage());
    }
}
