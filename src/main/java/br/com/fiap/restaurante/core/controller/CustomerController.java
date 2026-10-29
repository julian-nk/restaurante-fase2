package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;
import br.com.fiap.restaurante.core.dto.CustomerOutputDto;
import br.com.fiap.restaurante.core.dto.UpdateCustomerInputDto;
import br.com.fiap.restaurante.core.usecase.DeleteCustomerUseCase;
import br.com.fiap.restaurante.core.usecase.GetCustomerByIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetCustomersUseCase;
import br.com.fiap.restaurante.core.usecase.RegisterCustomerUseCase;
import br.com.fiap.restaurante.core.usecase.UpdateCustomerUseCase;

import java.util.List;

public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final GetCustomersUseCase getCustomersUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    public CustomerController(
            RegisterCustomerUseCase registerCustomerUseCase,
            GetCustomersUseCase getCustomersUseCase,
            GetCustomerByIdUseCase getCustomerByIdUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase
    ) {
        this.registerCustomerUseCase = registerCustomerUseCase;
        this.getCustomersUseCase = getCustomersUseCase;
        this.getCustomerByIdUseCase = getCustomerByIdUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
    }

    public CustomerOutputDto register(CreateCustomerInputDto input) {
        return registerCustomerUseCase.execute(input);
    }

    public List<CustomerOutputDto> findAll() {
        return getCustomersUseCase.execute();
    }

    public CustomerOutputDto findById(Long id) {
        return getCustomerByIdUseCase.execute(id);
    }

    public CustomerOutputDto update(UpdateCustomerInputDto input) {
        return updateCustomerUseCase.execute(input);
    }

    public void delete(Long id) {
        deleteCustomerUseCase.execute(id);
    }
}
