package ru.netology.Diploma_JD.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnauthorizedError extends RuntimeException{
        private String message;
        private int id;
    }
