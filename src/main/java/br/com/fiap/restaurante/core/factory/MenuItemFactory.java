package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.MenuItem;
import br.com.fiap.restaurante.core.dto.CreateMenuItemInputDto;

import java.math.BigDecimal;

public interface MenuItemFactory {

    /** Cria um novo item de cardápio (ainda sem ID) a partir dos dados de entrada. */
    MenuItem create(CreateMenuItemInputDto input);

    /** Reconstrói um item de cardápio já existente, por exemplo a partir do banco. */
    MenuItem restore(
            Long id,
            Long restaurantId,
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    );
}
