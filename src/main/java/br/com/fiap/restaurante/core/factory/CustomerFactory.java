package br.com.fiap.restaurante.core.factory;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.domain.Customer;
import br.com.fiap.restaurante.core.dto.CreateCustomerInputDto;

import java.time.OffsetDateTime;

public interface CustomerFactory {

    /**
     * Cria o cliente a partir dos dados do cadastro.
     *
     * @param personId id da pessoa já cadastrada com o mesmo CPF (que é dona de restaurante), ou nulo para uma
     *                 pessoa nova
     */
    Customer create(Long personId, CreateCustomerInputDto input);

    /** Reconstrói um cliente já existente, a partir dos dados da pessoa e do papel gravados no banco. */
    Customer restore(Long id, String name, String email, String cpf, Address address, OffsetDateTime createdOn);
}
