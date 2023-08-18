package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.model.TokenUser;
import ru.netology.cloudstorage.model.User;

import java.util.Optional;

@Repository
public interface TokenUserRepository extends JpaRepository<TokenUser, Long> {


    Optional<TokenUser> findByAuthToken(String authToken);

    void deleteByAuthToken (String authToken);


}
