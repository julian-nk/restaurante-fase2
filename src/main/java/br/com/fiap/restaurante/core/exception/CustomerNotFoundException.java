package br.com.fiap.restaurante.core.exception;

public class CustomerNotFoundException extends SystemBaseException {

    public CustomerNotFoundException(Long personId) {
        super("Cliente não encontrado para a pessoa com ID: " + personId);
    }

    @Override
    public String getCode() {
        return "customer.notFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 404;
    }
}
