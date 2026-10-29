package br.com.fiap.restaurante.core.exception;

public class UserTypeNotFoundException extends SystemBaseException {

    public UserTypeNotFoundException(Long id) {
        super("Tipo de usuário não encontrado com o ID: " + id);
    }

    @Override
    public String getCode() {
        return "userType.notFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 404;
    }
}
