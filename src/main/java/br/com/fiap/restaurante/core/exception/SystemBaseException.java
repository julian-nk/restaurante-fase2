package br.com.fiap.restaurante.core.exception;

/**
 * Exceção base do core. Cada exceção concreta informa um código estável e o status HTTP
 * equivalente, para que a camada web consiga traduzi-la sem que o core dependa do Spring.
 */
public abstract class SystemBaseException extends RuntimeException {

    protected SystemBaseException(String message) {
        super(message);
    }

    protected SystemBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String getCode();

    public abstract Integer getHttpStatus();
}
