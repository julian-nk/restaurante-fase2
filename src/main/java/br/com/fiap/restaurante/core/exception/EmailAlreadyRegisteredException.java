package br.com.fiap.restaurante.core.exception;

public class EmailAlreadyRegisteredException extends SystemBaseException {

    public EmailAlreadyRegisteredException(String email) {
        super("Já existe uma pessoa cadastrada com o e-mail: " + email);
    }

    @Override
    public String getCode() {
        return "person.emailAlreadyRegistered";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
