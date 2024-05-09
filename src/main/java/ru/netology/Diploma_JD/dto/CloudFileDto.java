package ru.netology.Diploma_JD.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@AllArgsConstructor
@Data
public class CloudFileDto {
    private String filename;
    private Integer size; //File size in bytes

    // для тестирования
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudFileDto that)) return false;
        return Objects.equals(getFilename(), that.getFilename()) && Objects.equals(getSize(), that.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilename(), getSize());
    }

    @Override
    public String toString() {
        return filename + ", " + size;
    }
}
