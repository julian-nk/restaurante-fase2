package br.com.fiap.restaurante.core.exception;

public class CpfAlreadyRegisteredException extends SystemBaseException {

    public CpfAlreadyRegisteredException(String cpf) {
        super("Já existe uma pessoa cadastrada com o CPF: " + cpf);
    }

    @Override
    public String getCode() {
        return "person.cpfAlreadyRegistered";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
