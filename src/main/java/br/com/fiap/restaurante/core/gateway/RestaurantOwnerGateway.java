package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;

import java.util.List;
import java.util.Optional;

public interface RestaurantOwnerGateway {

    /**
     * Grava o dono junto com os dados da pessoa e o endereço. Se o dono ainda não tiver id, a pessoa é
     * criada; se tiver, os dados dela são atualizados.
     */
    RestaurantOwner save(RestaurantOwner owner);

    Optional<RestaurantOwner> findById(Long id);

    List<RestaurantOwner> findAll();

    boolean existsById(Long id);

    /** Remove só o papel de dono de restaurante; a pessoa continua cadastrada. */
    void deleteById(Long id);
}
