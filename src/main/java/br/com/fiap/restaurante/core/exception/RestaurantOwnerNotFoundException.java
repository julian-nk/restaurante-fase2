package br.com.fiap.restaurante.core.exception;

public class RestaurantOwnerNotFoundException extends SystemBaseException {

    public RestaurantOwnerNotFoundException(Long personId) {
        super("Dono de restaurante não encontrado para a pessoa com ID: " + personId);
    }

    @Override
    public String getCode() {
        return "restaurantOwner.notFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 404;
    }
}
