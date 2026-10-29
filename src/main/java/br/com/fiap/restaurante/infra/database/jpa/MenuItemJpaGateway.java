package br.com.fiap.restaurante.infra.database.jpa;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.gateway.MenuItemGateway;
import br.com.fiap.restaurante.infra.database.jpa.repository.MenuItemRepository;
import br.com.fiap.restaurante.infra.database.mapper.MenuItemMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MenuItemJpaGateway extends JpaGatewaySupport implements MenuItemGateway {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper mapper;

    public MenuItemJpaGateway(MenuItemRepository menuItemRepository, MenuItemMapper mapper) {
        this.menuItemRepository = menuItemRepository;
        this.mapper = mapper;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        return call(() -> mapper.map(menuItemRepository.save(mapper.map(menuItem))));
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return call(() -> menuItemRepository.findById(id).map(mapper::map));
    }

    @Override
    public List<MenuItem> findAll() {
        return call(() -> menuItemRepository.findAll().stream().map(mapper::map).toList());
    }

    @Override
    public List<MenuItem> findByRestaurantId(Long restaurantId) {
        return call(() -> menuItemRepository.findByRestaurantId(restaurantId).stream().map(mapper::map).toList());
    }

    @Override
    public void deleteById(Long id) {
        run(() -> menuItemRepository.deleteById(id));
    }
}
