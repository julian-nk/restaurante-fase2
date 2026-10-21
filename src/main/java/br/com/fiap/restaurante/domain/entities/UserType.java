package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_type")
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 50)
    private String name;

    @Setter
    @Column(nullable = false, length = 30)
    private String code;

    public UserType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public void update(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
