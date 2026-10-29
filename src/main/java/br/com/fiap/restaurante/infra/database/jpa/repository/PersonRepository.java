package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmailIgnoreCase(String email);
}
