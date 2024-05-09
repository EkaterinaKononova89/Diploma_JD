package ru.netology.Diploma_JD.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.Diploma_JD.dto.JwtRequest;
import ru.netology.Diploma_JD.dto.JwtResponse;
import ru.netology.Diploma_JD.handler.ExceptionController;
import ru.netology.Diploma_JD.resolver.JwtRequestParam;
import ru.netology.Diploma_JD.service.AuthService;

@RestController
@AllArgsConstructor
@RequestMapping("/cloud")
public class AuthControllerImpl extends ExceptionController implements AuthController {
    private final AuthService authService; // завязан на интерфейс, а не конкретную реализацию


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody @JwtRequestParam JwtRequest authRequest) {
        return new ResponseEntity<>(authService.createAuthenticationToken(authRequest), HttpStatus.OK);
    }

    @PostMapping("/logout")
    // По умолчанию Spring Security поддерживает конечную точку /logout, поэтому дополнительный код не требуется.
    // (т.е. в базовом варианте этот эндпоинт вообще не должен быть в моем коде, верно понимаю?)
    // НО: в цепочке фильтров LogoutFilter появляется перед AuthorizationFilter, т.е. выход происходит ДО
    // авторизации/получения токена.
    // В задании указано, что /logout должен удалять/деактивировать токен, а это будет возможно только ПОСЛЕ авторизации.
    // Поэтому в настройках SecurityConfiguration отключаю встроенный разлогин - logout(AbstractHttpConfigurer::disable);

    public ResponseEntity<Void> logout(@RequestHeader("Auth-Token") String tokenWithBearer) {
        authService.deleteAuthenticationToken(tokenWithBearer);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

