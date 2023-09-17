package ru.netology.cloudstorage.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.netology.cloudstorage.dto.request.AuthenticationRQ;
import ru.netology.cloudstorage.dto.response.AppError;
import ru.netology.cloudstorage.dto.response.AuthenticationRS;
import ru.netology.cloudstorage.model.TokenUser;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.TokenUserRepository;
import ru.netology.cloudstorage.utils.JwtTokenUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final TokenUserRepository tokenUserRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    public ResponseEntity<?> login(@RequestBody @NotNull AuthenticationRQ authenticationRQ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRQ.getLogin(), authenticationRQ.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Некорректный логин или пароль"),
                    HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authenticationRQ.getLogin());
        String authToken = jwtTokenUtils.generateToken(userDetails);
        log.info("Пользователь {} авторизован. Токен: {}", authenticationRQ.getLogin(), authToken);
        tokenUserRepository.save(new TokenUser(userDetails.getUsername(), authToken));
        return ResponseEntity.ok(new AuthenticationRS(authToken));
    }

    public void logout(String authToken) {
        final String authTokenWithoutBearer = authToken.split(" ")[1];
        Optional<TokenUser> byAuthToken = tokenUserRepository.findByAuthToken(authTokenWithoutBearer);
        tokenUserRepository.deleteById(byAuthToken.get().getId());
    }

}


