package br.com.fiap.restaurante.core.gateway;

import java.util.Optional;

/**
 * Operações sobre a pessoa que não dependem do papel. A pessoa é gravada e lida junto com o papel
 * (cliente ou dono de restaurante), pelos gateways de cada um; aqui ficam só as consultas e a exclusão.
 */
public interface PersonGateway {

    boolean existsById(Long id);

    /** @return o id da pessoa com esse CPF, se houver */
    Optional<Long> findIdByCpf(String cpf);

    /**
     * Indica se o e-mail já pertence a outra pessoa.
     *
     * @param personId a pessoa que vai usar o e-mail, ou nulo se ela ainda não existe (aí qualquer pessoa conta)
     */
    boolean isEmailUsedByAnotherPerson(String email, Long personId);

    /** Remove a pessoa e o seu endereço. O banco remove em cascata os papéis e o usuário de acesso dela. */
    void deleteById(Long id);
}
