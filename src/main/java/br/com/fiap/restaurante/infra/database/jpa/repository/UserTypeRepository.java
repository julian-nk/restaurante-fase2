package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTypeRepository extends JpaRepository<UserTypeEntity, Long> {

    boolean existsByCode(String code);

    @Query("""
            select ut
            from UserTypeEntity ut, AppUserTypeEntity aut
            where aut.userTypeId = ut.id
              and aut.appUserId = :appUserId
            """)
    List<UserTypeEntity> findByAppUserId(@Param("appUserId") Long appUserId);
}
