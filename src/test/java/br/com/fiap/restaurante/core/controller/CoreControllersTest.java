package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.*;
import br.com.fiap.restaurante.core.usecase.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoreControllersTest {

    @Test
    void userTypeControllerShouldDelegateToUseCases() {
        var create = mock(CreateUserTypeUseCase.class);
        var getAll = mock(GetUserTypesUseCase.class);
        var getById = mock(GetUserTypeByIdUseCase.class);
        var update = mock(UpdateUserTypeUseCase.class);
        var delete = mock(DeleteUserTypeUseCase.class);
        var controller = new UserTypeController(create, getAll, getById, update, delete);

        var output = new UserTypeOutputDto(1L, "Cliente", "customer");
        var createInput = new CreateUserTypeInputDto("Cliente", "customer");
        var updateInput = new UpdateUserTypeInputDto(1L, "Cliente", "customer");
        when(create.execute(createInput)).thenReturn(output);
        when(getAll.execute()).thenReturn(List.of(output));
        when(getById.execute(1L)).thenReturn(output);
        when(update.execute(updateInput)).thenReturn(output);

        assertSame(output, controller.create(createInput));
        assertEquals(List.of(output), controller.findAll());
        assertSame(output, controller.findById(1L));
        assertSame(output, controller.update(updateInput));
        controller.delete(1L);

        verify(delete).execute(1L);
    }

    @Test
    void menuItemControllerShouldDelegateToUseCases() {
        var create = mock(CreateMenuItemUseCase.class);
        var getAll = mock(GetMenuItemsUseCase.class);
        var getById = mock(GetMenuItemByIdUseCase.class);
        var getByRestaurant = mock(GetMenuItemsByRestaurantIdUseCase.class);
        var update = mock(UpdateMenuItemUseCase.class);
        var delete = mock(DeleteMenuItemUseCase.class);
        var controller = new MenuItemController(create, getAll, getById, getByRestaurant, update, delete);

        var output = new MenuItemOutputDto(1L, 1L, "Pizza", "Pizza de queijo", BigDecimal.TEN, false, null);
        var createInput = new CreateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", BigDecimal.TEN, false, null);
        var updateInput = new UpdateMenuItemInputDto(1L, "Pizza", "Pizza de queijo", BigDecimal.TEN, false, null);
        when(create.execute(createInput)).thenReturn(output);
        when(getAll.execute()).thenReturn(List.of(output));
        when(getById.execute(1L)).thenReturn(output);
        when(getByRestaurant.execute(1L)).thenReturn(List.of(output));
        when(update.execute(updateInput)).thenReturn(output);

        assertSame(output, controller.create(createInput));
        assertEquals(List.of(output), controller.findAll());
        assertSame(output, controller.findById(1L));
        assertEquals(List.of(output), controller.findByRestaurantId(1L));
        assertSame(output, controller.update(updateInput));
        controller.delete(1L);

        verify(delete).execute(1L);
    }

    @Test
    void appUserTypeControllerShouldDelegateToUseCases() {
        var associate = mock(AssociateUserTypeUseCase.class);
        var getByAppUser = mock(GetUserTypesByAppUserUseCase.class);
        var remove = mock(RemoveUserTypeFromAppUserUseCase.class);
        var controller = new AppUserTypeController(associate, getByAppUser, remove);

        var output = new UserTypeOutputDto(2L, "Cliente", "customer");
        when(getByAppUser.execute(1L)).thenReturn(List.of(output));

        controller.associate(1L, 2L);
        assertEquals(List.of(output), controller.findUserTypes(1L));
        controller.remove(1L, 2L);

        verify(associate).execute(new AppUserTypeInputDto(1L, 2L));
        verify(remove).execute(new AppUserTypeInputDto(1L, 2L));
    }
}
