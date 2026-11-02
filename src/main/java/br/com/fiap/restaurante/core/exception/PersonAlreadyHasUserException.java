package br.com.fiap.restaurante.core.exception;

public class PersonAlreadyHasUserException extends SystemBaseException {

    public PersonAlreadyHasUserException(Long personId) {
        super("A pessoa com ID " + personId + " já possui um usuário.");
    }

    @Override
    public String getCode() {
        return "appUser.personAlreadyHasUser";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
