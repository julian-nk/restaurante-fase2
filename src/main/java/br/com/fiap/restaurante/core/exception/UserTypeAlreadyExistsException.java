package br.com.fiap.restaurante.core.exception;

public class UserTypeAlreadyExistsException extends SystemBaseException {

    public UserTypeAlreadyExistsException(String code) {
        super("Já existe um tipo de usuário com o código: " + code);
    }

    @Override
    public String getCode() {
        return "userType.alreadyExists";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
