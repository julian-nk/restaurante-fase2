
package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.exceptions.BusinessException;
import br.com.fiap.restaurante.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Tipo de usuário não encontrado");

        var response = handler.handleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(
                Map.of(
                        "error", "Not Found",
                        "message", "Tipo de usuário não encontrado"
                ),
                response.getBody()
        );
    }

    @Test
    void shouldHandleBusinessException() {
        BusinessException exception =
                new BusinessException("Tipo de usuário está associado");

        var response = handler.handleBusinessException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                Map.of(
                        "error", "Conflict",
                        "message", "Tipo de usuário está associado"
                ),
                response.getBody()
        );
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException exception =
                new IllegalArgumentException("ID inválido");

        var response = handler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                Map.of(
                        "error", "Bad Request",
                        "message", "ID inválido"
                ),
                response.getBody()
        );
    }

    @Test
    void shouldHandleValidationException() {
        var bindingResult = new BeanPropertyBindingResult(
                new Object(),
                "request"
        );

        bindingResult.addError(
                new FieldError(
                        "request",
                        "name",
                        "",
                        false,
                        null,
                        null,
                        "não deve estar em branco"
                )
        );

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "Validation Failed",
                response.getBody().get("error")
        );
        assertEquals(
                "name: não deve estar em branco",
                response.getBody().get("message")
        );
    }
}
