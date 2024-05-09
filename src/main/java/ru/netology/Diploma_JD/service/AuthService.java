package ru.netology.Diploma_JD.service;

import ru.netology.Diploma_JD.dto.JwtRequest;
import ru.netology.Diploma_JD.dto.JwtResponse;

public interface AuthService {
    JwtResponse createAuthenticationToken(JwtRequest authRequest);

    void deleteAuthenticationToken(String tokenWithBearer);
}
