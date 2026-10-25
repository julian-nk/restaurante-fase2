
package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.entities.MenuItem;
import br.com.fiap.restaurante.domain.usecases.CreateMenuItemUseCase;
import br.com.fiap.restaurante.domain.usecases.DeleteMenuItemUseCase;
import br.com.fiap.restaurante.domain.usecases.GetMenuItemByIdUseCase;
import br.com.fiap.restaurante.domain.usecases.GetMenuItemsByRestaurantIdUseCase;
import br.com.fiap.restaurante.domain.usecases.GetMenuItemsUseCase;
import br.com.fiap.restaurante.domain.usecases.UpdateMenuItemUseCase;
import br.com.fiap.restaurante.dtos.CreateMenuItemRequest;
import br.com.fiap.restaurante.dtos.MenuItemResponse;
import br.com.fiap.restaurante.dtos.UpdateMenuItemRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final GetMenuItemsUseCase getMenuItemsUseCase;
    private final GetMenuItemByIdUseCase getMenuItemByIdUseCase;
    private final GetMenuItemsByRestaurantIdUseCase getMenuItemsByRestaurantIdUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;

    public MenuItemController(
            CreateMenuItemUseCase createMenuItemUseCase,
            GetMenuItemsUseCase getMenuItemsUseCase,
            GetMenuItemByIdUseCase getMenuItemByIdUseCase,
            GetMenuItemsByRestaurantIdUseCase getMenuItemsByRestaurantIdUseCase,
            UpdateMenuItemUseCase updateMenuItemUseCase,
            DeleteMenuItemUseCase deleteMenuItemUseCase
    ) {
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.getMenuItemsUseCase = getMenuItemsUseCase;
        this.getMenuItemByIdUseCase = getMenuItemByIdUseCase;
        this.getMenuItemsByRestaurantIdUseCase = getMenuItemsByRestaurantIdUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(
            @Valid @RequestBody CreateMenuItemRequest request
    ) {
        MenuItem menuItem = createMenuItemUseCase.execute(
                request.restaurantId(),
                request.name(),
                request.description(),
                request.price(),
                request.dineInOnly(),
                request.photoUrl()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(menuItem));
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> findAll() {
        List<MenuItemResponse> response = getMenuItemsUseCase.execute()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> findById(
            @PathVariable Long id
    ) {
        MenuItem menuItem = getMenuItemByIdUseCase.execute(id);

        return ResponseEntity.ok(toResponse(menuItem));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemResponse>> findByRestaurantId(
            @PathVariable Long restaurantId
    ) {
        List<MenuItemResponse> response =
                getMenuItemsByRestaurantIdUseCase.execute(restaurantId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemRequest request
    ) {
        MenuItem menuItem = updateMenuItemUseCase.execute(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.dineInOnly(),
                request.photoUrl()
        );

        return ResponseEntity.ok(toResponse(menuItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        deleteMenuItemUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    private MenuItemResponse toResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isDineInOnly(),
                menuItem.getPhotoUrl()
        );
    }
}
