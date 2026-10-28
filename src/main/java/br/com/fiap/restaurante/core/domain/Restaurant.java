package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
public class Restaurant {

    private static final Comparator<OpeningHours> BY_DAY_AND_TIME = Comparator
            .comparing(OpeningHours::getDayOfWeek)
            .thenComparing(OpeningHours::getOpeningTime);

    private final Long id;
    private final String cnpj;
    private final Address address;
    private String name;
    private String cuisineType;
    private Long ownerId;
    private List<OpeningHours> openingHours;

    @Builder
    public Restaurant(Long id, String cnpj, Address address, String name, String cuisineType, Long ownerId,
                      List<OpeningHours> openingHours) {
        this.id = id;
        this.cnpj = cnpj;
        this.address = address;
        this.name = name;
        this.cuisineType = cuisineType;
        this.ownerId = ownerId;
        this.openingHours = validateOpeningHours(openingHours);
    }

    private static List<OpeningHours> validateOpeningHours(List<OpeningHours> openingHours) {
        if (openingHours == null || openingHours.isEmpty()) {
            throw new InvalidFieldException("O restaurante deve ter pelo menos um horário de funcionamento.");
        }
        for (int i = 0; i < openingHours.size(); i++) {
            for (int j = i + 1; j < openingHours.size(); j++) {
                OpeningHours first = openingHours.get(i);
                OpeningHours second = openingHours.get(j);
                if (first.overlaps(second)) {
                    throw new InvalidFieldException("Os horários de funcionamento não podem se sobrepor: "
                                                    + first.describe() + " e " + second.describe() + ".");
                }
            }
        }
        return openingHours.stream().sorted(BY_DAY_AND_TIME).toList();
    }

    public void update(String name, String cuisineType, Long ownerId) {
        this.name = name;
        this.cuisineType = cuisineType;
        this.ownerId = ownerId;
    }

    public void updateAddress(
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            StatesEnum state,
            String zipCode
    ) {
        address.update(street, number, complement, neighborhood, city, state, zipCode);
    }

    public void replaceOpeningHours(List<OpeningHours> openingHours) {
        this.openingHours = validateOpeningHours(openingHours);
    }
}
