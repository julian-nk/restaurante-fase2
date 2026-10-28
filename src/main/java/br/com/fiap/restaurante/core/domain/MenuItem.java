package br.com.fiap.restaurante.core.domain;

import br.com.fiap.restaurante.core.exception.InvalidFieldException;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
public class MenuItem {

    static final int NAME_MAX_LENGTH = 150;
    static final int PHOTO_URL_MAX_LENGTH = 500;

    private final Long id;
    private final Long restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean dineInOnly;
    private String photoUrl;

    MenuItem(
            Long id,
            Long restaurantId,
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    ) {
        if (restaurantId == null || restaurantId <= 0) {
            throw new InvalidFieldException("O ID do restaurante deve ser maior que zero.");
        }
        validate(name, description, price, photoUrl);
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name.trim();
        this.description = description.trim();
        this.price = price;
        this.dineInOnly = dineInOnly;
        this.photoUrl = photoUrl;
    }

    public void update(
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    ) {
        validate(name, description, price, photoUrl);
        this.name = name.trim();
        this.description = description.trim();
        this.price = price;
        this.dineInOnly = dineInOnly;
        this.photoUrl = photoUrl;
    }

    private void validate(String name, String description, BigDecimal price, String photoUrl) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException("O nome do prato é obrigatório.");
        }
        if (name.trim().length() > NAME_MAX_LENGTH) {
            throw new InvalidFieldException(
                    "O nome do prato deve ter no máximo " + NAME_MAX_LENGTH + " caracteres.");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidFieldException("A descrição do prato é obrigatória.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFieldException("O preço deve ser maior ou igual a zero.");
        }
        if (photoUrl != null && photoUrl.length() > PHOTO_URL_MAX_LENGTH) {
            throw new InvalidFieldException(
                    "A URL da foto deve ter no máximo " + PHOTO_URL_MAX_LENGTH + " caracteres.");
        }
    }

}
