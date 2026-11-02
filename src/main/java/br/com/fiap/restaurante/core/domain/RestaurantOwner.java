package br.com.fiap.restaurante.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class RestaurantOwner extends Person {

    private OffsetDateTime createdOn;
    private String businessEmail;
    private List<Restaurant> ownedRestaurants;

    @Builder
    public RestaurantOwner(String cpf, Long id, String name, String email, Address address, OffsetDateTime createdOn,
                           String businessEmail, List<Restaurant> ownedRestaurants) {
        super(cpf, id, name, email, address);
        this.createdOn = DomainValidation.requireNonNull(createdOn, "data de cadastro");
        this.businessEmail = DomainValidation.requireEmail(businessEmail, "e-mail comercial");
        this.ownedRestaurants = ownedRestaurants;
    }

    public void addRestaurant(Restaurant restaurant) {
        this.ownedRestaurants.add(restaurant);
    }

    public void addRestaurants(List<Restaurant> restaurants) {
        this.ownedRestaurants.addAll(restaurants);
    }

    public void removeRestaurant(Restaurant restaurant) {
        this.ownedRestaurants.remove(restaurant);
    }

    public void updateBusinessEmail(String businessEmail) {
        this.businessEmail = DomainValidation.requireEmail(businessEmail, "e-mail comercial");
    }

}
