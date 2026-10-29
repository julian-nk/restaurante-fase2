package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.OpeningHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface OpeningHoursRepository extends JpaRepository<OpeningHoursEntity, Long> {

    List<OpeningHoursEntity> findByRestaurantId(Long restaurantId);

    List<OpeningHoursEntity> findByRestaurantIdIn(Collection<Long> restaurantIds);

    /**
     * Exclusão em lote
     */
    @Transactional
    @Modifying
    @Query("delete from OpeningHoursEntity o where o.restaurantId = :restaurantId")
    void deleteAllByRestaurantId(@Param("restaurantId") Long restaurantId);
}
