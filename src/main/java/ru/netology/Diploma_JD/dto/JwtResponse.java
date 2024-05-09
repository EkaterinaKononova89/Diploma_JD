package ru.netology.Diploma_JD.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class) //эта аннотация переделает "authToken" в "auth-token"
public class JwtResponse {
    private String authToken; // нужно auth-token

    public String toString() {
        return authToken;
    }
}
