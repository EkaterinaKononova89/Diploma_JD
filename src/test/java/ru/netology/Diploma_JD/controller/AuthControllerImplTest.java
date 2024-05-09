package ru.netology.Diploma_JD.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import ru.netology.Diploma_JD.dto.JwtRequest;
import ru.netology.Diploma_JD.dto.JwtResponse;
import ru.netology.Diploma_JD.error.CloudBadCredentials;
import ru.netology.Diploma_JD.service.AuthServiceImpl;

public class AuthControllerImplTest {
    AuthControllerImpl sut;
    AuthServiceImpl authService = Mockito.mock(AuthServiceImpl.class);

    @BeforeEach
    public void beforeEach() {
        sut = new AuthControllerImpl(authService);
    }

    @AfterEach
    public void afterEach() {
        sut = null;
    }

    @Test
    public void createAuthenticationTokenTest_200_OK() {
        // given
        JwtRequest authRequest = new JwtRequest("user1@mail.ru", "123");
        JwtResponse response = new JwtResponse("some-token-string-for-user1");

        Mockito.when(authService.createAuthenticationToken(authRequest)).thenReturn(response);

        // when
        ResponseEntity<JwtResponse> resultResponse = sut.createAuthenticationToken(authRequest);

        // then
        Assertions.assertEquals("<200 OK OK,some-token-string-for-user1,[]>", resultResponse.toString());
    }

    @Test
    public void createAuthenticationTokenTest_error400() {
        // given
        JwtRequest authRequest = new JwtRequest("user1@mail.ru", "123");

        Mockito.when(authService.createAuthenticationToken(authRequest)).thenThrow
                (new CloudBadCredentials("CloudBadCredentials: Неверные имя и пароль", 1));

        // when

        // then
        CloudBadCredentials exception = Assertions.assertThrows(CloudBadCredentials.class, () -> {
            sut.createAuthenticationToken(authRequest);
        });
        Assertions.assertEquals("CloudBadCredentials: Неверные имя и пароль", exception.getMessage());
    }

    @Test
    public void deleteAuthenticationTokenTest() {
        // given
        String tokenWithBearer = "Bearer: some-token-string-for-user1";

        Mockito.doNothing().when(authService).deleteAuthenticationToken(Mockito.anyString());

        // when
        ResponseEntity<Void> response = sut.logout(tokenWithBearer);

        // then
        Assertions.assertEquals("<200 OK OK,[]>", response.toString());
    }
}
