package br.com.fiap.restaurante.core.exception;

public class PersonNotFoundException extends SystemBaseException {

    public PersonNotFoundException(Long id) {
        super("Pessoa não encontrada com o ID: " + id);
    }

    @Override
    public String getCode() {
        return "person.notFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 404;
    }
}
