package br.com.fiap.restaurante.core.exception;

public class InvalidPasswordException extends SystemBaseException {

    public InvalidPasswordException() {
        super("A senha atual está incorreta.");
    }

    @Override
    public String getCode() {
        return "appUser.invalidPassword";
    }

    @Override
    public Integer getHttpStatus() {
        return 422;
    }
}
