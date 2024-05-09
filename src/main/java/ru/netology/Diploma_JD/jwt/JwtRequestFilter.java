package ru.netology.Diploma_JD.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.Diploma_JD.error.UnauthorizedError;
import ru.netology.Diploma_JD.repository.ErrorRepository;
import ru.netology.Diploma_JD.repository.TokenRepository;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@CommonsLog
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenRepository tokenRepository;
    private final ErrorRepository errorRepository;

    @Override // добавляю фильтр в цепочку и помещаю в контекст
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = null;
        if (request.getHeader("Authorization") != null) {
            authHeader = request.getHeader("Authorization");
        } else if (request.getHeader("Auth-Token") != null) {
            authHeader = request.getHeader("Auth-token");
        }
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            if (!tokenRepository.getAuthTokenSet().contains(jwt)) {
                log.error("401: Токен не действителен");
                throw new UnauthorizedError("UnauthorizedError: Токен не действителен", errorRepository.setNumber()); // не вылавливается хендлером
            }

            try {
                username = jwtTokenUtils.getNameFromToken(jwt);
            } catch (ExpiredJwtException e) { // если время подписи истекло
                throw new UnauthorizedError("ВРЕМЯ ЖИЗНИ ТОКЕНА ИСТЕКЛО", errorRepository.setNumber());
            } catch (MalformedJwtException ex) { // если подпись некорректная (не парсится)
                throw new UnauthorizedError("НЕКОРРЕКТНАЯ ПОДПИСЬ", errorRepository.setNumber());
            } catch (SignatureException e) { // если подпись не совпадает с вычисленной
                throw new UnauthorizedError("подпись не совпадает с вычисленной", errorRepository.setNumber());
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtTokenUtils.getRolesFromToken(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}