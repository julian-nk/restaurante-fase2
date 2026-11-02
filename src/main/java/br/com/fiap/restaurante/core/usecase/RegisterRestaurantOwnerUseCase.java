package br.com.fiap.restaurante.core.usecase;

import br.com.fiap.restaurante.core.dto.CreateRestaurantOwnerInputDto;
import br.com.fiap.restaurante.core.dto.LoginInputDto;
import br.com.fiap.restaurante.core.dto.RestaurantOwnerOutputDto;

public interface RegisterRestaurantOwnerUseCase {

    /**
     * Cadastra o dono de restaurante. O usuário de acesso da pessoa recebe o tipo {@code restaurant_owner}; se ela
     * ainda não tem usuário, ele é criado com o login e a senha informados.
     */
    RestaurantOwnerOutputDto execute(CreateRestaurantOwnerInputDto input, LoginInputDto login);
}
