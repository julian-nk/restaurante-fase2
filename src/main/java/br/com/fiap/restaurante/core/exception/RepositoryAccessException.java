package br.com.fiap.restaurante.core.exception;

public class RepositoryAccessException extends SystemBaseException {

    public RepositoryAccessException(Throwable cause) {
        super("Erro ao acessar o repositório de dados.", cause);
    }

    @Override
    public String getCode() {
        return "repository.accessError";
    }

    @Override
    public Integer getHttpStatus() {
        return 500;
    }
}
