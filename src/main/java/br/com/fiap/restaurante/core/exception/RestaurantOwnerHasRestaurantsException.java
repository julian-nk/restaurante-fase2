package br.com.fiap.restaurante.core.exception;

public class RestaurantOwnerHasRestaurantsException extends SystemBaseException {

    public RestaurantOwnerHasRestaurantsException(Long personId) {
        super("A pessoa com ID " + personId + " é dona de restaurantes cadastrados. Transfira ou remova os restaurantes antes.");
    }

    @Override
    public String getCode() {
        return "restaurantOwner.hasRestaurants";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
