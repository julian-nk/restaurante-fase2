package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
public class Restaurant {

    static final int NAME_MAX_LENGTH = 150;
    static final int CUISINE_TYPE_MAX_LENGTH = 50;
    static final int CNPJ_LENGTH = 14;

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
        this.cnpj = DomainValidation.requireDigits(cnpj, CNPJ_LENGTH, "CNPJ");
        this.address = DomainValidation.requireNonNull(address, "endereço");
        update(name, cuisineType, ownerId);
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

    /** Valida os três campos antes de alterar qualquer um. */
    public void update(String name, String cuisineType, Long ownerId) {
        String validName = DomainValidation.requireText(name, NAME_MAX_LENGTH, "nome");
        String validCuisineType = DomainValidation.requireText(cuisineType, CUISINE_TYPE_MAX_LENGTH, "tipo de cozinha");
        Long validOwnerId = DomainValidation.requirePositive(ownerId, "ID do dono");
        this.name = validName;
        this.cuisineType = validCuisineType;
        this.ownerId = validOwnerId;
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
