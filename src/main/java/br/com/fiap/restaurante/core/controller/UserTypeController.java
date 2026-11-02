package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.CreateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UpdateUserTypeInputDto;
import br.com.fiap.restaurante.core.dto.UserTypeOutputDto;
import br.com.fiap.restaurante.core.usecase.CreateUserTypeUseCase;
import br.com.fiap.restaurante.core.usecase.DeleteUserTypeUseCase;
import br.com.fiap.restaurante.core.usecase.GetUserTypeByIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetUserTypesUseCase;
import br.com.fiap.restaurante.core.usecase.UpdateUserTypeUseCase;

import java.util.List;

public class UserTypeController {

    private final CreateUserTypeUseCase createUserTypeUseCase;
    private final GetUserTypesUseCase getUserTypesUseCase;
    private final GetUserTypeByIdUseCase getUserTypeByIdUseCase;
    private final UpdateUserTypeUseCase updateUserTypeUseCase;
    private final DeleteUserTypeUseCase deleteUserTypeUseCase;

    public UserTypeController(
            CreateUserTypeUseCase createUserTypeUseCase,
            GetUserTypesUseCase getUserTypesUseCase,
            GetUserTypeByIdUseCase getUserTypeByIdUseCase,
            UpdateUserTypeUseCase updateUserTypeUseCase,
            DeleteUserTypeUseCase deleteUserTypeUseCase
    ) {
        this.createUserTypeUseCase = createUserTypeUseCase;
        this.getUserTypesUseCase = getUserTypesUseCase;
        this.getUserTypeByIdUseCase = getUserTypeByIdUseCase;
        this.updateUserTypeUseCase = updateUserTypeUseCase;
        this.deleteUserTypeUseCase = deleteUserTypeUseCase;
    }

    public UserTypeOutputDto create(CreateUserTypeInputDto input) {
        return createUserTypeUseCase.execute(input);
    }

    public List<UserTypeOutputDto> findAll() {
        return getUserTypesUseCase.execute();
    }

    public UserTypeOutputDto findById(Long id) {
        return getUserTypeByIdUseCase.execute(id);
    }

    public UserTypeOutputDto update(UpdateUserTypeInputDto input) {
        return updateUserTypeUseCase.execute(input);
    }

    public void delete(Long id) {
        deleteUserTypeUseCase.execute(id);
    }
}
