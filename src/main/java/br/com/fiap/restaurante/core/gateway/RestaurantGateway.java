package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantGateway {

    /** Salva o restaurante com o endereço e os horários de funcionamento. */
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    List<Restaurant> findAll();

    boolean existsById(Long id);

    boolean existsByCnpj(String cnpj);

    boolean existsByOwnerId(Long ownerId);

    /** Remove o restaurante, os horários de funcionamento e o endereço. */
    void deleteById(Long id);
}
