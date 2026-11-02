package br.com.fiap.restaurante.core.exception;

public class InvalidFieldException extends SystemBaseException {

    public InvalidFieldException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return "validation.invalidField";
    }

    @Override
    public Integer getHttpStatus() {
        return 400;
    }
}
