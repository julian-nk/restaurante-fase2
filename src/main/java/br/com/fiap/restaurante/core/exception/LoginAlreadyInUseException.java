package br.com.fiap.restaurante.core.exception;

public class LoginAlreadyInUseException extends SystemBaseException {

    public LoginAlreadyInUseException(String login) {
        super("O login já está em uso: " + login);
    }

    @Override
    public String getCode() {
        return "appUser.loginAlreadyInUse";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
