package ru.netology.Diploma_JD.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorUploadFile extends RuntimeException{
    private String message;
    private int id;
}
