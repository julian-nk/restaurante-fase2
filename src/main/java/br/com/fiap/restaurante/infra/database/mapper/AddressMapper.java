package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.Address;
import br.com.fiap.restaurante.core.factory.AddressFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    private final AddressFactory addressFactory;

    public AddressMapper(AddressFactory addressFactory) {
        this.addressFactory = addressFactory;
    }

    public Address map(AddressEntity entity) {
        return addressFactory.restore(
                entity.getId(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState(),
                entity.getZipCode()
        );
    }

    public AddressEntity map(Address address) {
        return new AddressEntity(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState().getValue(),
                address.getZipCode()
        );
    }
}
