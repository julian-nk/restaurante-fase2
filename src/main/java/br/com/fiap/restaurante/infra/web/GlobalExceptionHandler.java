package br.com.fiap.restaurante.infra.web;

import br.com.fiap.restaurante.core.exception.SystemBaseException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Traduz as exceções do core em respostas HTTP. O core informa o status em cada exceção
 * ({@link SystemBaseException#getHttpStatus()}), então este handler é o único ponto que
 * conhece o Spring.
 *
 * <p>Precisa vir antes do {@code ProblemDetailsExceptionHandler} que o Spring Boot registra com
 * {@code spring.mvc.problem-details.enabled=true}: ele tem {@code @Order(0)} e também trata
 * {@link MethodArgumentNotValidException}, o que trocaria o formato dos erros de validação por Problem Details.
 * As demais exceções do Spring MVC (JSON malformado, método não suportado etc.) continuam com ele.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemBaseException.class)
    public ResponseEntity<Map<String, String>> handleSystemException(SystemBaseException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getHttpStatus());

        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "error", status.getReasonPhrase(),
                        "code", exception.getCode(),
                        "message", exception.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        String messages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "Validation Failed",
                        "message", messages
                ));
    }
}
