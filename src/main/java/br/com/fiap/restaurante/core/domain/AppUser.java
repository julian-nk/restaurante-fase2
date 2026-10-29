package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Usuário de acesso ao sistema. Pertence a uma pessoa e guarda os tipos de usuário que tem.
 *
 * <p>A pessoa e os tipos são referenciados pelo id, e não pelo objeto, porque cada um tem cadastro e ciclo de
 * vida próprios: o usuário não altera nem grava nenhum deles. O que pertence ao usuário é a lista de quais
 * tipos ele tem, e ela só muda por {@link #addUserType(Long)} e {@link #removeUserType(Long)}.
 */
@Getter
public class AppUser {

    static final int LOGIN_MIN_LENGTH = 3;
    static final int LOGIN_MAX_LENGTH = 50;
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private static final int PASSWORD_HASH_MAX_LENGTH = 255;
    private static final String USER_TYPE_ID_FIELD = "ID do tipo de usuário";

    private final Long id;
    private final Long personId;
    private final String login;
    private String passwordHash;
    private final SortedSet<Long> userTypeIds = new TreeSet<>();

    /**
     * @param userTypeIds tipos que o usuário já tem; pode ser nulo ou vazio (usuário sem tipos)
     */
    @Builder
    public AppUser(Long id, Long personId, String login, String passwordHash, Set<Long> userTypeIds) {
        this.id = id;
        this.personId = DomainValidation.requirePositive(personId, "ID da pessoa");
        this.login = validateLogin(login);
        this.passwordHash = DomainValidation.requireText(passwordHash, PASSWORD_HASH_MAX_LENGTH, "senha");
        if (userTypeIds != null) {
            for (Long userTypeId : userTypeIds) {
                this.userTypeIds.add(DomainValidation.requirePositive(userTypeId, USER_TYPE_ID_FIELD));
            }
        }
    }

    private static String validateLogin(String login) {
        String value = DomainValidation.requireText(login, LOGIN_MAX_LENGTH, "login").toLowerCase(Locale.ROOT);
        if (value.length() < LOGIN_MIN_LENGTH) {
            throw new InvalidFieldException("O campo login deve ter no mínimo " + LOGIN_MIN_LENGTH + " caracteres.");
        }
        if (!LOGIN_PATTERN.matcher(value).matches()) {
            throw new InvalidFieldException("O login só pode conter letras, números, ponto, hífen e sublinhado.");
        }
        return value;
    }

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = DomainValidation.requireText(newPasswordHash, PASSWORD_HASH_MAX_LENGTH, "senha");
    }

    /** Ids dos tipos do usuário, em ordem crescente. A coleção é só para leitura. */
    public Set<Long> getUserTypeIds() {
        return Collections.unmodifiableSet(userTypeIds);
    }

    public boolean hasUserType(Long userTypeId) {
        return userTypeIds.contains(userTypeId);
    }

    /**
     * Dá o tipo ao usuário. Se ele já tiver o tipo, nada muda.
     *
     * @return {@code true} se o tipo foi adicionado, {@code false} se o usuário já o tinha
     * @throws InvalidFieldException se o id não for positivo
     */
    public boolean addUserType(Long userTypeId) {
        return userTypeIds.add(DomainValidation.requirePositive(userTypeId, USER_TYPE_ID_FIELD));
    }

    /**
     * Tira o tipo do usuário. Se ele não tiver o tipo, nada muda.
     *
     * @return {@code true} se o tipo foi removido, {@code false} se o usuário não o tinha
     */
    public boolean removeUserType(Long userTypeId) {
        return userTypeIds.remove(userTypeId);
    }
}
