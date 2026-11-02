package br.com.fiap.restaurante.core.exception;

public class NotRestaurantOwnerException extends SystemBaseException {

    public NotRestaurantOwnerException() {
        super("Somente o dono do restaurante ou um administrador pode fazer esta operação.");
    }

    @Override
    public String getCode() {
        return "restaurant.notOwner";
    }

    @Override
    public Integer getHttpStatus() {
        return 403;
    }
}
