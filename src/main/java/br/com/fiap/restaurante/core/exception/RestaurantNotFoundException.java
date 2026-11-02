package br.com.fiap.restaurante.core.exception;

public class RestaurantNotFoundException extends SystemBaseException {

    public RestaurantNotFoundException(Long id) {
        super("Restaurante não encontrado com o ID: " + id);
    }

    @Override
    public String getCode() {
        return "restaurant.notFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 404;
    }
}
