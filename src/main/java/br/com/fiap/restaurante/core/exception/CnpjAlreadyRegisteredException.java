package br.com.fiap.restaurante.core.exception;

public class CnpjAlreadyRegisteredException extends SystemBaseException {

    public CnpjAlreadyRegisteredException() {
        super("Já existe um restaurante cadastrado com esse CNPJ.");
    }

    @Override
    public String getCode() {
        return "restaurant.cnpjAlreadyRegistered";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
