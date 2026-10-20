package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.entities.UserType;
import br.com.fiap.restaurante.domain.usecases.CreateUserTypeUseCase;
import br.com.fiap.restaurante.dtos.CreateUserTypeRequest;
import br.com.fiap.restaurante.dtos.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-types")
public class UserTypeController {

    private final CreateUserTypeUseCase createUserTypeUseCase;

    public UserTypeController(
            CreateUserTypeUseCase createUserTypeUseCase) {
        this.createUserTypeUseCase = createUserTypeUseCase;
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
}