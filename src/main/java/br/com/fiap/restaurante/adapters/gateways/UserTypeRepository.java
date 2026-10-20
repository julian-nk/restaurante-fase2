package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    boolean existsByCode(String code);
}
