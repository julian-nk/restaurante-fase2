package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.*;
import br.com.fiap.restaurante.core.usecase.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationControllersTest {

    @Test
    void appUserControllerShouldDelegateToUseCases() {
        var create = mock(CreateAppUserUseCase.class);
        var getAll = mock(GetAppUsersUseCase.class);
        var getById = mock(GetAppUserByIdUseCase.class);
        var changePassword = mock(ChangeAppUserPasswordUseCase.class);
        var delete = mock(DeleteAppUserUseCase.class);
        var controller = new AppUserController(create, getAll, getById, changePassword, delete);

        var output = new AppUserOutputDto(1L, 10L, "maria");
        var createInput = new CreateAppUserInputDto(10L, "maria", "senhaForte1");
        var passwordInput = new ChangePasswordInputDto(1L, "senhaForte1", "senhaNova2");
        when(create.execute(createInput)).thenReturn(output);
        when(getAll.execute()).thenReturn(List.of(output));
        when(getById.execute(1L)).thenReturn(output);

        assertSame(output, controller.create(createInput));
        assertEquals(List.of(output), controller.findAll());
        assertSame(output, controller.findById(1L));
        controller.changePassword(passwordInput);
        controller.delete(1L);
        verify(changePassword).execute(passwordInput);
        verify(delete).execute(1L);
    }

    @Test
    void customerControllerShouldDelegateToUseCases() {
        var register = mock(RegisterCustomerUseCase.class);
        var getAll = mock(GetCustomersUseCase.class);
        var getById = mock(GetCustomerByIdUseCase.class);
        var update = mock(UpdateCustomerUseCase.class);
        var delete = mock(DeleteCustomerUseCase.class);
        var controller = new CustomerController(register, getAll, getById, update, delete);

        var output = new CustomerOutputDto(5L, "Maria", "maria@email.com", "52998224725", null, OffsetDateTime.now());
        var registerInput = new CreateCustomerInputDto("Maria", "maria@email.com", "52998224725", null);
        var updateInput = new UpdateCustomerInputDto(5L, "Maria", "maria@email.com", null);
        when(register.execute(registerInput)).thenReturn(output);
        when(getAll.execute()).thenReturn(List.of(output));
        when(getById.execute(5L)).thenReturn(output);
        when(update.execute(updateInput)).thenReturn(output);

        assertSame(output, controller.register(registerInput));
        assertEquals(List.of(output), controller.findAll());
        assertSame(output, controller.findById(5L));
        assertSame(output, controller.update(updateInput));
        controller.delete(5L);
        verify(delete).execute(5L);
    }

    @Test
    void restaurantOwnerControllerShouldDelegateToUseCases() {
        var register = mock(RegisterRestaurantOwnerUseCase.class);
        var getAll = mock(GetRestaurantOwnersUseCase.class);
        var getById = mock(GetRestaurantOwnerByIdUseCase.class);
        var update = mock(UpdateRestaurantOwnerUseCase.class);
        var delete = mock(DeleteRestaurantOwnerUseCase.class);
        var controller = new RestaurantOwnerController(register, getAll, getById, update, delete);

        var output = new RestaurantOwnerOutputDto(
                5L, "Maria", "maria@email.com", "52998224725", null, "contato@cantina.com", OffsetDateTime.now());
        var registerInput = new CreateRestaurantOwnerInputDto(
                "Maria", "maria@email.com", "52998224725", null, "contato@cantina.com");
        var updateInput = new UpdateRestaurantOwnerInputDto(5L, "Maria", "maria@email.com", null, "contato@cantina.com");
        when(register.execute(registerInput)).thenReturn(output);
        when(getAll.execute()).thenReturn(List.of(output));
        when(getById.execute(5L)).thenReturn(output);
        when(update.execute(updateInput)).thenReturn(output);

        assertSame(output, controller.register(registerInput));
        assertEquals(List.of(output), controller.findAll());
        assertSame(output, controller.findById(5L));
        assertSame(output, controller.update(updateInput));
        controller.delete(5L);
        verify(delete).execute(5L);
    }

    @Test
    void restaurantControllerShouldDelegateToUseCases() {
        var create = mock(CreateRestaurantUseCase.class);
        var getAll = mock(GetRestaurantsUseCase.class);
        var getById = mock(GetRestaurantByIdUseCase.class);
        var update = mock(UpdateRestaurantUseCase.class);
        var delete = mock(DeleteRestaurantUseCase.class);
        var controller = new RestaurantController(create, getAll, getById, update, delete);

        var output = new RestaurantOutputDto(1L, "Cantina", "12345678000199", "Italiana", 7L, null, List.of());
        var createInput = new CreateRestaurantInputDto("Cantina", "12345678000199", "Italiana", 7L, null, List.of());
        var updateInput = new UpdateRestaurantInputDto(1L, "Cantina", "Italiana", 7L, null, List.of());
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
}
