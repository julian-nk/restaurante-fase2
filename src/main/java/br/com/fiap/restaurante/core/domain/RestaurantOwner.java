package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RestaurantOwner extends Person {

    private OffsetDateTime createdOn;
    private String businessEmail;
    private final List<Restaurant> ownedRestaurants = new ArrayList<>();

    @Builder
    public RestaurantOwner(String cpf, Long id, String name, String email, Address address, OffsetDateTime createdOn,
                           String businessEmail, List<Restaurant> ownedRestaurants) {
        super(cpf, id, name, email, address);
        this.createdOn = DomainValidation.requireNonNull(createdOn, "data de cadastro");
        this.businessEmail = DomainValidation.requireEmail(businessEmail, "e-mail comercial");
        if (ownedRestaurants != null) {
            addRestaurants(ownedRestaurants);
        }
    }

    /**
     * Restaurantes de que a pessoa é dona. A lista não pode ser alterada por fora: use {@link #addRestaurant} e
     * {@link #removeRestaurant}, que conferem se o restaurante é mesmo deste dono.
     */
    public List<Restaurant> getOwnedRestaurants() {
        return Collections.unmodifiableList(ownedRestaurants);
    }

    public boolean hasRestaurants() {
        return !ownedRestaurants.isEmpty();
    }

    /**
     * Acrescenta o restaurante à lista. Se ele já estiver nela (mesmo ID), a versão antiga é substituída.
     *
     * @throws InvalidFieldException se o restaurante for de outro dono
     */
    public void addRestaurant(Restaurant restaurant) {
        DomainValidation.requireNonNull(restaurant, "restaurante");
        if (!restaurant.isOwnedBy(getId())) {
            throw new InvalidFieldException("O restaurante " + restaurant.getName() + " não pertence a este dono.");
        }
        removeRestaurant(restaurant);
        this.ownedRestaurants.add(restaurant);
    }

    public void addRestaurants(List<Restaurant> restaurants) {
        restaurants.forEach(this::addRestaurant);
    }

    /** Tira o restaurante da lista. Restaurantes já gravados são comparados pelo ID. */
    public void removeRestaurant(Restaurant restaurant) {
        this.ownedRestaurants.removeIf(owned -> owned == restaurant
                || owned.getId() != null && owned.getId().equals(restaurant.getId()));
    }

    public void updateBusinessEmail(String businessEmail) {
        this.businessEmail = DomainValidation.requireEmail(businessEmail, "e-mail comercial");
    }

}
