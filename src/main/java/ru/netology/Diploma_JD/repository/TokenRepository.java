package ru.netology.Diploma_JD.repository;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.Diploma_JD.jwt.JwtTokenUtils;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Data
@Transactional
public class TokenRepository { // хранение InMemory

    @Value("${tokenRepository.size}")
    private int setSize;
    private final Set<String> authTokenSet = ConcurrentHashMap.newKeySet(); // Set, т.к. многопоточный List не подходит при частом изменении данных
    private final JwtTokenUtils jwtTokenUtils;

    public TokenRepository(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public void cleanAuthTokenSet() { // для очистки просроченных токенов (тех, кто не разлогинился)
        if (authTokenSet.size() == setSize) {
            for (String token : authTokenSet) {
                Date dateOfExpiration = jwtTokenUtils.expireDate(token);
                if (dateOfExpiration.getTime() < System.currentTimeMillis()) {
                    authTokenSet.remove(token);
                }
            }
        }
    }
}