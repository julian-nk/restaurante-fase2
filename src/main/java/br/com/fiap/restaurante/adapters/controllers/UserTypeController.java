package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.domain.usecases.*;
import br.com.fiap.restaurante.dtos.CreateUserTypeRequest;
import br.com.fiap.restaurante.dtos.UpdateUserTypeRequest;
import br.com.fiap.restaurante.dtos.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/user-types")
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

    @PostMapping
    public ResponseEntity<UserTypeResponse> create(
            @Valid @RequestBody CreateUserTypeRequest request) {

        UserType userType = createUserTypeUseCase.execute(
                request.name(),
                request.code()
        );

        UserTypeResponse response = new UserTypeResponse(
                userType.getId(),
                userType.getName(),
                userType.getCode()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserTypeResponse>> findAll() {

        List<UserTypeResponse> response =
                getUserTypesUseCase.execute()
                        .stream()
                        .map(userType -> new UserTypeResponse(
                                userType.getId(),
                                userType.getName(),
                                userType.getCode()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTypeResponse> findById(
            @PathVariable Long id) {

        return getUserTypeByIdUseCase.execute(id)
                .map(userType -> new UserTypeResponse(
                        userType.getId(),
                        userType.getName(),
                        userType.getCode()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserTypeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserTypeRequest request) {

        return updateUserTypeUseCase.execute(
                        id,
                        request.name(),
                        request.code()
                )
                .map(userType -> new UserTypeResponse(
                        userType.getId(),
                        userType.getName(),
                        userType.getCode()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        boolean deleted = deleteUserTypeUseCase.execute(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}