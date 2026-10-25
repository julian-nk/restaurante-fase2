
package br.com.fiap.restaurante.interfaces;

import br.com.fiap.restaurante.domain.entities.MenuItem;

import java.util.List;
import java.util.Optional;

public interface IMenuItemGateway {

    MenuItem save(MenuItem menuItem);

    Optional<MenuItem> findById(Long id);

    List<MenuItem> findAll();

    List<MenuItem> findByRestaurantId(Long restaurantId);

    void deleteById(Long id);
}
