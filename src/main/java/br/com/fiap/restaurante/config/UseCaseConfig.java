package br.com.fiap.restaurante.config;

import br.com.fiap.restaurante.domain.usecases.CreateUserTypeUseCase;
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
}
