package br.com.fiap.restaurante.core.controller;

import br.com.fiap.restaurante.core.dto.AppUserOutputDto;
import br.com.fiap.restaurante.core.dto.ChangePasswordInputDto;
import br.com.fiap.restaurante.core.dto.CreateAppUserInputDto;
import br.com.fiap.restaurante.core.usecase.ChangeAppUserPasswordUseCase;
import br.com.fiap.restaurante.core.usecase.CreateAppUserUseCase;
import br.com.fiap.restaurante.core.usecase.DeleteAppUserUseCase;
import br.com.fiap.restaurante.core.usecase.GetAppUserByIdUseCase;
import br.com.fiap.restaurante.core.usecase.GetAppUsersUseCase;

import java.util.List;

public class AppUserController {

    private final CreateAppUserUseCase createAppUserUseCase;
    private final GetAppUsersUseCase getAppUsersUseCase;
    private final GetAppUserByIdUseCase getAppUserByIdUseCase;
    private final ChangeAppUserPasswordUseCase changeAppUserPasswordUseCase;
    private final DeleteAppUserUseCase deleteAppUserUseCase;

    public AppUserController(
            CreateAppUserUseCase createAppUserUseCase,
            GetAppUsersUseCase getAppUsersUseCase,
            GetAppUserByIdUseCase getAppUserByIdUseCase,
            ChangeAppUserPasswordUseCase changeAppUserPasswordUseCase,
            DeleteAppUserUseCase deleteAppUserUseCase
    ) {
        this.createAppUserUseCase = createAppUserUseCase;
        this.getAppUsersUseCase = getAppUsersUseCase;
        this.getAppUserByIdUseCase = getAppUserByIdUseCase;
        this.changeAppUserPasswordUseCase = changeAppUserPasswordUseCase;
        this.deleteAppUserUseCase = deleteAppUserUseCase;
    }

    public AppUserOutputDto create(CreateAppUserInputDto input) {
        return createAppUserUseCase.execute(input);
    }

    public List<AppUserOutputDto> findAll() {
        return getAppUsersUseCase.execute();
    }

    public AppUserOutputDto findById(Long id) {
        return getAppUserByIdUseCase.execute(id);
    }

    public void changePassword(ChangePasswordInputDto input) {
        changeAppUserPasswordUseCase.execute(input);
    }

    public void delete(Long id) {
        deleteAppUserUseCase.execute(id);
    }
}
