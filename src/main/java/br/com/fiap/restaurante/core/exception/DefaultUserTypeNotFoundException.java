package br.com.fiap.restaurante.core.exception;

/**
 * Quem ganha o papel de cliente ou de dono recebe o tipo de usuário do papel ({@code customer} ou
 * {@code restaurant_owner}). Se o tipo não estiver cadastrado, o cadastro é recusado até que ele seja recriado.
 */
public class DefaultUserTypeNotFoundException extends SystemBaseException {

    public DefaultUserTypeNotFoundException(String code) {
        super("O tipo de usuário com o código " + code + " não está cadastrado. Ele é dado a todos que recebem "
              + "o papel correspondente; cadastre-o de novo para continuar.");
    }

    @Override
    public String getCode() {
        return "userType.defaultNotFound";
    }

    @Override
    public Integer getHttpStatus() {
        return 409;
    }
}
