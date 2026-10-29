package br.com.fiap.restaurante.core.exception;

public class RestaurantOwnerAlreadyRegisteredException extends SystemBaseException {

    public RestaurantOwnerAlreadyRegisteredException(Long personId) {
        super("A pessoa com ID " + personId + " já é dona de restaurante.");
    }

    @Override
    public String getCode() {
        return "restaurantOwner.alreadyRegistered";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
