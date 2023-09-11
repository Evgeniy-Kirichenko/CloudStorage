package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import ru.netology.cloudstorage.repository.TokenUserRepository;
import ru.netology.cloudstorage.utils.JwtTokenUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.cloudstorage.TestData.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private TokenUserRepository tokenUserRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenUtils jwtTokenUtils;
    @Mock
    private UserService userService;

    @Test
    void login() {
        Mockito.when(userService.loadUserByUsername(USERNAME_1)).thenReturn(USER_1_DET);
        Mockito.when(jwtTokenUtils.generateToken(USER_1_DET)).thenReturn(TOKEN_1);
        assertEquals(ResponseEntity.ok(AUTHENTICATION_RS), authenticationService.login(AUTHENTICATION_RQ));
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(USERNAME_PASSWORD_AUTHENTICATION_TOKEN);
    }

}
