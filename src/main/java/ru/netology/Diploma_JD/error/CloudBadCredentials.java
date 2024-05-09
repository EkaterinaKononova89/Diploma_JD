package ru.netology.Diploma_JD.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CloudBadCredentials extends RuntimeException {
    private String message;
    private int id;
}
