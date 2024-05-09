package ru.netology.Diploma_JD.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class ErrorRepositoryImplTest {
    ErrorRepositoryImpl sut;

    @BeforeEach
    public void beforeEach() {
        sut = new ErrorRepositoryImpl();
    }

    @AfterEach
    public void afterEach() {
        sut = null;
    }

    @Test
    public void setNumberTest() {
        // given
        sut.getId().set(1);
        //when
        int result = sut.setNumber();

        //then
        Assert.assertEquals(result, 2);
    }
}
