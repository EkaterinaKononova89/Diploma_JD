package ru.netology.Diploma_JD.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.netology.Diploma_JD.dto.JwtRequest;
import ru.netology.Diploma_JD.dto.JwtResponse;

public interface AuthController {
    ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authRequest);

    ResponseEntity<Void> logout(@RequestHeader("Auth-Token") String tokenWithBearer);
}
