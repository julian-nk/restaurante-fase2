package br.com.fiap.restaurante.core.gateway;

import br.com.fiap.restaurante.core.domain.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerGateway {

    /**
     * Grava o cliente junto com os dados da pessoa e o endereço. Se o cliente ainda não tiver id, a pessoa
     * é criada; se tiver, os dados dela são atualizados.
     */
    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    List<Customer> findAll();

    boolean existsById(Long id);

    /** Remove só o papel de cliente; a pessoa continua cadastrada. */
    void deleteById(Long id);
}
