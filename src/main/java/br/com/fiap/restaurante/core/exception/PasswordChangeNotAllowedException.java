package br.com.fiap.restaurante.core.exception;

/**
 * Cada usuário só troca a própria senha: o ID informado precisa ser o do usuário logado.
 */
public class PasswordChangeNotAllowedException extends SystemBaseException {

    public PasswordChangeNotAllowedException() {
        super("Só é possível trocar a senha do próprio usuário.");
    }

    @Override
    public String getCode() {
        return "appUser.passwordChangeNotAllowed";
    }

    @Override
    public Integer getHttpStatus() {
        return 403;
    }
}
