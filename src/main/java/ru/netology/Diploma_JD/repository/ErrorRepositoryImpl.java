package ru.netology.Diploma_JD.repository;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Getter
public class ErrorRepositoryImpl implements ErrorRepository {
    private final AtomicInteger id = new AtomicInteger(0);

    public int setNumber() {
        return id.incrementAndGet();
    }
}