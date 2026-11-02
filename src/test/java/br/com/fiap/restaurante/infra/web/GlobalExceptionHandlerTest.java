package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.exception.RepositoryAccessException;
import br.com.fiap.restaurante.core.exception.UserTypeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldUseStatusAndCodeFromCoreException() {
        var response = handler.handleSystemException(new UserTypeNotFoundException(7L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Map.of(
                "error", "Not Found",
                "code", "userType.notFound",
                "message", "Tipo de usuário não encontrado com o ID: 7"
        ), response.getBody());
    }

    @Test
    void shouldReturnInternalServerErrorForRepositoryFailure() {
        var response = handler.handleSystemException(new RepositoryAccessException(new IllegalStateException()));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("repository.accessError", response.getBody().get("code"));
    }

    @Test
    void shouldJoinBeanValidationErrors() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "", false, null, null, "não deve estar em branco"));
        bindingResult.addError(new FieldError("request", "code", "", false, null, null, "não deve estar em branco"));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertEquals("name: não deve estar em branco; code: não deve estar em branco", response.getBody().get("message"));
    }
}
