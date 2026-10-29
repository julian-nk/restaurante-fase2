package br.com.fiap.restaurante.core.exception;

public class MenuItemNotFoundException extends SystemBaseException {

    public MenuItemNotFoundException(Long id) {
        super("Prato não encontrado com o ID: " + id);
    }

    @Override
    public String getCode() {
        return "menuItem.notFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 404;
    }
}
