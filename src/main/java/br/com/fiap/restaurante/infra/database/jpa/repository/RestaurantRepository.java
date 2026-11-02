package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByOwnerId(Long ownerId);

    List<RestaurantEntity> findByOwnerIdInOrderByIdAsc(Collection<Long> ownerIds);
}
