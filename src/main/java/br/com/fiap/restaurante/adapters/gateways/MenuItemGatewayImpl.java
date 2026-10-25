
package br.com.fiap.restaurante.adapters.gateways;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.interfaces.IMenuItemGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MenuItemGatewayImpl implements IMenuItemGateway {

    private final MenuItemRepository repository;

    public MenuItemGatewayImpl(MenuItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        return repository.save(menuItem);
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<MenuItem> findAll() {
        return repository.findAll();
    }

    @Override
    public List<MenuItem> findByRestaurantId(Long restaurantId) {
        return repository.findByRestaurantId(restaurantId);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
