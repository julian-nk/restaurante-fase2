
package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "dine_in_only", nullable = false)
    private boolean dineInOnly = false;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    protected MenuItem() {
    }

    public MenuItem(
            Long restaurantId,
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    ) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.dineInOnly = dineInOnly;
        this.photoUrl = photoUrl;
    }

    public Long getId() {
        return id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isDineInOnly() {
        return dineInOnly;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void update(
            String name,
            String description,
            BigDecimal price,
            boolean dineInOnly,
            String photoUrl
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.dineInOnly = dineInOnly;
        this.photoUrl = photoUrl;
    }
}
