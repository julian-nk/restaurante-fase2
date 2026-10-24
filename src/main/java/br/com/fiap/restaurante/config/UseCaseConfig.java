package br.com.fiap.restaurante.config;

import br.com.fiap.restaurante.domain.usecases.*;
import br.com.fiap.restaurante.interfaces.IAppUserTypeGateway;
import br.com.fiap.restaurante.interfaces.IUserTypeGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateUserTypeUseCase createUserTypeUseCase(
            IUserTypeGateway gateway) {
        return new CreateUserTypeUseCase(gateway);
    }

    @Bean
    public GetUserTypesUseCase getUserTypesUseCase(
            IUserTypeGateway gateway) {
        return new GetUserTypesUseCase(gateway);
    }

    @Bean
    public GetUserTypeByIdUseCase getUserTypeByIdUseCase(
            IUserTypeGateway gateway) {
        return new GetUserTypeByIdUseCase(gateway);
    }

    @Bean
    public UpdateUserTypeUseCase updateUserTypeUseCase(
            IUserTypeGateway gateway) {
        return new UpdateUserTypeUseCase(gateway);
    }

    @Bean
    public DeleteUserTypeUseCase deleteUserTypeUseCase(
            IUserTypeGateway gateway) {
        return new DeleteUserTypeUseCase(gateway);
    }

    @Bean
    public AssociateUserTypeUseCase associateUserTypeUseCase(
            IAppUserTypeGateway appUserTypeGateway,
            IUserTypeGateway userTypeGateway) {
        return new AssociateUserTypeUseCase(
                appUserTypeGateway,
                userTypeGateway
        );
    }

    @Bean
    public GetUserTypesByAppUserUseCase getUserTypesByAppUserUseCase(
            IAppUserTypeGateway appUserTypeGateway) {
        return new GetUserTypesByAppUserUseCase(appUserTypeGateway);
    }

    @Bean
    public RemoveUserTypeFromAppUserUseCase removeUserTypeFromAppUserUseCase(
            IAppUserTypeGateway appUserTypeGateway,
            IUserTypeGateway userTypeGateway) {
        return new RemoveUserTypeFromAppUserUseCase(
                appUserTypeGateway,
                userTypeGateway
        );
    }
}
