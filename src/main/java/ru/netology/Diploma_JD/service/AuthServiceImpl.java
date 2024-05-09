package ru.netology.Diploma_JD.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.Diploma_JD.dto.JwtRequest;
import ru.netology.Diploma_JD.dto.JwtResponse;
import ru.netology.Diploma_JD.error.CloudBadCredentials;
import ru.netology.Diploma_JD.repository.ErrorRepository;
import ru.netology.Diploma_JD.repository.TokenRepository;
import ru.netology.Diploma_JD.jwt.JwtTokenUtils;

@Service
@AllArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final CloudServiceImpl cloudService;
    private final TokenRepository tokenRepository;
    private final ErrorRepository errorRepository;

    public JwtResponse createAuthenticationToken(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new CloudBadCredentials("CloudBadCredentials: Неверные имя и пароль", errorRepository.setNumber());
        }
        UserDetails userDetails = cloudService.loadUserByUsername(authRequest.getLogin());
        String token = jwtTokenUtils.generateToken(userDetails);
        tokenRepository.getAuthTokenSet().add(token); // добавляю в список только сам токен, без Bearer
        tokenRepository.cleanAuthTokenSet(); // проверка, не переполнился ли Set
        return new JwtResponse(token);
    }

    public void deleteAuthenticationToken(String tokenWithBearer) {
        String token = tokenWithBearer.substring(7);
        tokenRepository.getAuthTokenSet().remove(token);
    }
}
