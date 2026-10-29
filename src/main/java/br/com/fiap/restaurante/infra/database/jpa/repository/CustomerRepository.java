package br.com.fiap.restaurante.infra.database.jpa.repository;

import br.com.fiap.restaurante.infra.database.jpa.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
