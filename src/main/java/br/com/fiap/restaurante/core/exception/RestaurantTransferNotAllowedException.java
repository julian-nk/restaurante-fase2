package br.com.fiap.restaurante.core.exception;

public class RestaurantTransferNotAllowedException extends SystemBaseException {

    public RestaurantTransferNotAllowedException() {
        super("Somente um administrador pode transferir o restaurante para outro dono.");
    }

    @Override
    public String getCode() {
        return "restaurant.transferNotAllowed";
    }

    @Override
    public Integer getHttpStatus() {
        return 403;
    }
}
