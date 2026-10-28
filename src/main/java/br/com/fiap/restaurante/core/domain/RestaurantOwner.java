package br.com.fiap.restaurante.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class RestaurantOwner extends Person {

    private OffsetDateTime createdOn;
    private Long personId;
    private String businessEmail;
    private List<Restaurant> restaurants;

    @Builder
    public RestaurantOwner(String cpf, Long id, String name, String email, Address address, OffsetDateTime createdOn,
                           Long personId, String businessEmail, List<Restaurant> restaurants) {
        super(cpf, id, name, email, address);
        this.createdOn = createdOn;
        this.personId = personId;
        this.businessEmail = businessEmail;
        this.restaurants = restaurants;
    }

    public void addRestaurant(Restaurant restaurant) {
        this.restaurants.add(restaurant);
    }

    public void addRestaurants(List<Restaurant> restaurants) {
        this.restaurants.addAll(restaurants);
    }

    public void removeRestaurant(Restaurant restaurant) {
        this.restaurants.remove(restaurant);
    }

    public void updateBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

}
