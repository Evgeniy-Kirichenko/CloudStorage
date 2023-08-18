package ru.netology.cloudstorage.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "token")
public class TokenUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "login", nullable = false)
    String login;

    @Column(name = "auth_token", nullable = false)
    String authToken;

    public TokenUser(String login, String authToken) {
        this.login = login;
        this.authToken = authToken;
    }
}
