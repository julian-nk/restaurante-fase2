package br.com.fiap.restaurante.infra.database.mapper;

import br.com.fiap.restaurante.core.domain.OpeningHours;
import br.com.fiap.restaurante.core.domain.Restaurant;
import br.com.fiap.restaurante.core.factory.OpeningHoursFactory;
import br.com.fiap.restaurante.core.factory.RestaurantFactory;
import br.com.fiap.restaurante.infra.database.jpa.entity.AddressEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.OpeningHoursEntity;
import br.com.fiap.restaurante.infra.database.jpa.entity.RestaurantEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantMapper {

    private final RestaurantFactory restaurantFactory;
    private final OpeningHoursFactory openingHoursFactory;
    private final AddressMapper addressMapper;

    public RestaurantMapper(
            RestaurantFactory restaurantFactory,
            OpeningHoursFactory openingHoursFactory,
            AddressMapper addressMapper
    ) {
        this.restaurantFactory = restaurantFactory;
        this.openingHoursFactory = openingHoursFactory;
        this.addressMapper = addressMapper;
    }

    public Restaurant map(RestaurantEntity entity, AddressEntity address, List<OpeningHoursEntity> openingHours) {
        return restaurantFactory.restore(
                entity.getId(),
                entity.getName(),
                entity.getCnpj(),
                entity.getCuisineType(),
                entity.getOwnerId(),
                addressMapper.map(address),
                openingHours.stream()
                        .map(hours -> openingHoursFactory.restore(
                                hours.getId(), hours.getDayOfWeek(), hours.getOpeningTime(), hours.getClosingTime()))
                        .toList()
        );
    }

    public RestaurantEntity map(Restaurant restaurant, Long addressId) {
        return new RestaurantEntity(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCnpj(),
                restaurant.getCuisineType(),
                restaurant.getOwnerId(),
                addressId
        );
    }

    public List<OpeningHoursEntity> map(List<OpeningHours> openingHours, Long restaurantId) {
        return openingHours.stream()
                .map(hours -> new OpeningHoursEntity(
                        null, restaurantId, hours.getDayOfWeek(), hours.getOpeningTime(), hours.getClosingTime()))
                .toList();
    }
}
