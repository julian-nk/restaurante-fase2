package br.com.fiap.restaurante.config;

import br.com.fiap.restaurante.domain.usecases.*;
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
}
