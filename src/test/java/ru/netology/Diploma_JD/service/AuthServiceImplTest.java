package ru.netology.Diploma_JD.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ru.netology.Diploma_JD.dto.JwtRequest;
import ru.netology.Diploma_JD.error.CloudBadCredentials;
import ru.netology.Diploma_JD.repository.ErrorRepository;
import ru.netology.Diploma_JD.repository.TokenRepository;
import ru.netology.Diploma_JD.jwt.JwtTokenUtils;

public class AuthServiceImplTest {
    AuthServiceImpl sut;
    AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
    JwtTokenUtils jwtTokenUtils = Mockito.mock(JwtTokenUtils.class);
    CloudServiceImpl cloudService = Mockito.mock(CloudServiceImpl.class);
    TokenRepository tokenRepository = Mockito.mock(TokenRepository.class);
    ErrorRepository errorRepository = Mockito.mock(ErrorRepository.class);
    Authentication authentication = Mockito.mock(Authentication.class);


    @BeforeEach
    public void beforeEach() {
        sut = new AuthServiceImpl(authenticationManager, jwtTokenUtils, cloudService, tokenRepository, errorRepository);
    }

    @AfterEach
    public void afterEach() {
        sut = null;
    }

    @Test
    public void createAuthenticationTokenTest_OK() {
        // given
        JwtRequest authRequest = new JwtRequest();

        UserDetails user1 = User.withUsername("user1")
                .password("password1")
                .roles("USER")
                .build();

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(cloudService.loadUserByUsername(authRequest.getLogin())).thenReturn(user1);
        Mockito.when(jwtTokenUtils.generateToken(user1)).thenReturn("some-token-string-for-user1");

        // when
        var result = sut.createAuthenticationToken(authRequest);

        // then
        Assertions.assertEquals("some-token-string-for-user1", result.getAuthToken());
    }

    @Test
    public void createAuthenticationTokenTest_error() {
        // given
        JwtRequest authRequest = new JwtRequest();

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow
                (new BadCredentialsException("Неверные имя и пароль"));

        // when

        // then
        CloudBadCredentials exception = Assertions.assertThrows(CloudBadCredentials.class, () -> {
            sut.createAuthenticationToken(authRequest);
        });
        Assertions.assertEquals("CloudBadCredentials: Неверные имя и пароль", exception.getMessage());
    }
}
