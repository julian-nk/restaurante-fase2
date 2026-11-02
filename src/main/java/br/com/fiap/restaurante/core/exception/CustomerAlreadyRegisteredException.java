package br.com.fiap.restaurante.core.exception;

public class CustomerAlreadyRegisteredException extends SystemBaseException {

    public CustomerAlreadyRegisteredException(Long personId) {
        super("A pessoa com ID " + personId + " já é cliente.");
    }

    @Override
    public String getCode() {
        return "customer.alreadyRegistered";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
