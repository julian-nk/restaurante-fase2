package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.Person;

import java.util.List;
import java.util.Optional;

public interface PersonGateway {

    Person save(Person person);

    Optional<Person> findById(Long id);

    List<Person> findAll();

    boolean existsById(Long id);

    Optional<Long> findIdByCpf(String cpf);

    boolean isEmailUsedByAnotherPerson(String email, Long personId);

    /**
     * Remove a pessoa e o seu endereço. O banco remove em cascata os papéis e o usuário de acesso dela.
     */
    void deleteById(Long id);
}
