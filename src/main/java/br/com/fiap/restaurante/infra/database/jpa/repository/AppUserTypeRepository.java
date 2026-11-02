package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.AppUserTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AppUserTypeRepository extends JpaRepository<AppUserTypeEntity, AppUserTypeId> {

    List<AppUserTypeEntity> findByAppUserId(Long appUserId);

    List<AppUserTypeEntity> findByAppUserIdIn(Collection<Long> appUserIds);
}
