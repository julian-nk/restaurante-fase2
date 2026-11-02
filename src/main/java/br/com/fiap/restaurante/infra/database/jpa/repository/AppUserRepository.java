package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

    boolean existsByPersonId(Long personId);

    boolean existsByLoginIgnoreCase(String login);

    Optional<AppUserEntity> findByLoginIgnoreCase(String login);
}
