package br.com.fiap.restaurante.adapters.controllers;

import br.com.fiap.restaurante.domain.usecases.AssociateUserTypeUseCase;
import br.com.fiap.restaurante.domain.usecases.GetUserTypesByAppUserUseCase;
import br.com.fiap.restaurante.domain.usecases.RemoveUserTypeFromAppUserUseCase;
import br.com.fiap.restaurante.dtos.UserTypeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app-users/{appUserId}/types")
public class AppUserTypeController {

    private final AssociateUserTypeUseCase associateUserTypeUseCase;
    private final GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase;
    private final RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase;

    public AppUserTypeController(
            AssociateUserTypeUseCase associateUserTypeUseCase,
            GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase,
            RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase
    ) {
        this.associateUserTypeUseCase = associateUserTypeUseCase;
        this.getUserTypesByAppUserUseCase = getUserTypesByAppUserUseCase;
        this.removeUserTypeFromAppUserUseCase = removeUserTypeFromAppUserUseCase;
    }

    @PostMapping("/{userTypeId}")
    public ResponseEntity<Void> associate(
            @PathVariable Long appUserId,
            @PathVariable Long userTypeId
    ) {
        associateUserTypeUseCase.execute(appUserId, userTypeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserTypeResponse>> findByAppUser(
            @PathVariable Long appUserId
    ) {
        List<UserTypeResponse> response =
                getUserTypesByAppUserUseCase.execute(appUserId)
                        .stream()
                        .map(userType -> new UserTypeResponse(
                                userType.getId(),
                                userType.getName(),
                                userType.getCode()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userTypeId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long appUserId,
            @PathVariable Long userTypeId
    ) {
        removeUserTypeFromAppUserUseCase.execute(appUserId, userTypeId);
        return ResponseEntity.noContent().build();
    }
}