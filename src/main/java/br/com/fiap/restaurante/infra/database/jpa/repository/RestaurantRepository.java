package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByOwnerId(Long ownerId);
}
