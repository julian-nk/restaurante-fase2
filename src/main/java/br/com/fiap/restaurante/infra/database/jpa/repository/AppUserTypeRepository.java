package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserTypeRepository extends JpaRepository<AppUserTypeEntity, AppUserTypeId> {
}
