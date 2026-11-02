package br.com.fiap.restaurante.core.exception;

public class AppUserNotFoundException extends SystemBaseException {

    public AppUserNotFoundException(Long id) {
        super("Usuário não encontrado com o ID: " + id);
    }

    @Override
    public String getCode() {
        return "appUser.notFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 404;
    }
}
