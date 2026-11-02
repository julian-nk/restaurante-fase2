package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.factory.RestaurantOwnerFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantOwnerEntity;
import org.springframework.stereotype.Component;

@Component
public class RestaurantOwnerMapper {

    private final RestaurantOwnerFactory restaurantOwnerFactory;
    private final AddressMapper addressMapper;

    public RestaurantOwnerMapper(RestaurantOwnerFactory restaurantOwnerFactory, AddressMapper addressMapper) {
        this.restaurantOwnerFactory = restaurantOwnerFactory;
        this.addressMapper = addressMapper;
    }

    /** @param address pode ser nulo (pessoa sem endereço) */
    public RestaurantOwner map(PersonEntity person, AddressEntity address, RestaurantOwnerEntity owner) {
        return restaurantOwnerFactory.restore(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getCpf(),
                address == null ? null : addressMapper.map(address),
                owner.getCreatedOn(),
                owner.getBusinessEmail()
        );
    }

    /** @param personId o id da pessoa já gravada, que também é o id do dono */
    public RestaurantOwnerEntity map(Long personId, RestaurantOwner owner) {
        return new RestaurantOwnerEntity(personId, owner.getCreatedOn(), owner.getBusinessEmail());
    }
}
