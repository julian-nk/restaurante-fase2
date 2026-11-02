package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.domain.RestaurantOwner;
import br.com.fiap.restaurante.core.factory.RestaurantOwnerFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.PersonEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantOwnerEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantOwnerMapper {

    private final RestaurantOwnerFactory restaurantOwnerFactory;
    private final AddressMapper addressMapper;

    public RestaurantOwnerMapper(RestaurantOwnerFactory restaurantOwnerFactory, AddressMapper addressMapper) {
        this.restaurantOwnerFactory = restaurantOwnerFactory;
        this.addressMapper = addressMapper;
    }

    /**
     * @param address     pode ser nulo (pessoa sem endereço)
     * @param restaurants os restaurantes de que a pessoa é dona
     */
    public RestaurantOwner map(PersonEntity person, AddressEntity address, RestaurantOwnerEntity owner,
                               List<Restaurant> restaurants) {
        return restaurantOwnerFactory.restore(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getCpf(),
                address == null ? null : addressMapper.map(address),
                owner.getCreatedOn(),
                owner.getBusinessEmail(),
                restaurants
        );
    }

    /** @param personId o id da pessoa já gravada, que também é o id do dono */
    public RestaurantOwnerEntity map(Long personId, RestaurantOwner owner) {
        return new RestaurantOwnerEntity(personId, owner.getCreatedOn(), owner.getBusinessEmail());
    }
}
