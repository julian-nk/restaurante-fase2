package br.com.fiap.restaurante.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_id", nullable = false, unique = true)
    private Long personId;

    @Column(nullable = false, length = 50)
    private String login;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    protected AppUser() {
    }

    public AppUser(Long personId, String login, String passwordHash) {
        this.personId = personId;
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public Long getPersonId() {
        return personId;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void updateLogin(String login) {
        this.login = login;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
