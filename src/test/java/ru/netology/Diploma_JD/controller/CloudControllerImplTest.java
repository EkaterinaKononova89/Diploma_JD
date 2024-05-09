package ru.netology.Diploma_JD.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.Diploma_JD.dto.CloudFileDto;
import ru.netology.Diploma_JD.dto.FileName;
import ru.netology.Diploma_JD.dto.Role;
import ru.netology.Diploma_JD.entity.CloudFile;
import ru.netology.Diploma_JD.entity.CloudUser;
import ru.netology.Diploma_JD.error.*;
import ru.netology.Diploma_JD.service.CloudServiceImpl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class CloudControllerImplTest {
    CloudControllerImpl sut;
    CloudServiceImpl cloudService = Mockito.mock(CloudServiceImpl.class);

    @BeforeEach
    public void beforeEach() {
        sut = new CloudControllerImpl(cloudService);
    }

    @AfterEach
    public void afterEach() {
        sut = null;
    }

    @Test
    public void getAllFilesTest_200_OK() {
        // given
        int limit = 3;
        Principal principal = () -> "user1";
        List<CloudFileDto> listTest = Arrays.asList(
                new CloudFileDto("file1", 100),
                new CloudFileDto("file2", 200),
                new CloudFileDto("file3", 300));
        Mockito.when(cloudService.getAllFiles(limit, principal.getName())).thenReturn(listTest);

        // when
        ResponseEntity<List<CloudFileDto>> response = sut.getAllFiles(limit, principal);

        // then
        Assertions.assertEquals("<200 OK OK,[file1, 100, file2, 200, file3, 300],[]>", response.toString());
    }

    @Test
    public void getAllFilesTest_error400() {
        // given
        int limit = 0;
        Principal principal = () -> "user1";
        Mockito.when(cloudService.getAllFiles(limit, principal.getName())).thenThrow
                (new ErrorInputData("ErrorInputData: Размер отображаемого списка 0", 1));

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.getAllFiles(limit, principal);
        });
        Assertions.assertEquals("ErrorInputData: Размер отображаемого списка 0", exception.getMessage());
    }

    @Test
    public void getAllFilesTest_error500() {
        // given
        int limit = 3;
        Principal principal = () -> "user1";
        Mockito.when(cloudService.getAllFiles(limit, principal.getName())).thenThrow
                (new ErrorGettingFileList("ErrorGettingFileList: Неизвестная ошибка получения списка файлов", 1));

        // when

        // then
        ErrorGettingFileList exception = Assertions.assertThrows(ErrorGettingFileList.class, () -> {
            sut.getAllFiles(limit, principal);
        });
        Assertions.assertEquals("ErrorGettingFileList: Неизвестная ошибка получения списка файлов", exception.getMessage());
    }

    @Test
    public void uploadFileTest_200_OK() {
        // given
        MultipartFile uploadFile = Mockito.mock(MultipartFile.class);
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doNothing().when(cloudService).uploadFile(uploadFile, filename, principal);

        // when
        ResponseEntity<Void> response = sut.uploadFile(uploadFile, filename, principal);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void uploadFileTest_error400() {
        // given
        MultipartFile uploadFile = Mockito.mock(MultipartFile.class);
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorInputData("ErrorInputData: Возможно это дубликат: файл с таким именем, расширением" +
                " и размером уже существует", 1)).when(cloudService).uploadFile(uploadFile, filename, principal);

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.uploadFile(uploadFile, filename, principal);
        });
        Assertions.assertEquals("ErrorInputData: Возможно это дубликат: файл с таким именем, расширением" +
                " и размером уже существует", exception.getMessage());
    }

    @Test
    public void deleteFileTest_200_OK() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doNothing().when(cloudService).deleteFile(filename, principal);

        // when
        ResponseEntity<Void> response = sut.deleteFile(filename, principal);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteFileTest_error400() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorInputData("ErrorInputData: файл не найден", 1))
                .when(cloudService).deleteFile(filename, principal);

        // when

        // then
        ErrorInputData exceptions = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.deleteFile(filename, principal);
        });
        Assertions.assertEquals("ErrorInputData: файл не найден", exceptions.getMessage());
    }

    @Test
    public void deleteFileTest_error500() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorDeleteFile("ErrorDeleteFile: неизвестная ошибка удаления файла", 1))
                .when(cloudService).deleteFile(filename, principal);

        // when

        // then
        ErrorDeleteFile exceptions = Assertions.assertThrows(ErrorDeleteFile.class, () -> {
            sut.deleteFile(filename, principal);
        });
        Assertions.assertEquals("ErrorDeleteFile: неизвестная ошибка удаления файла", exceptions.getMessage());
    }

    @Test
    public void getFileTest_200_OK() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        String encodeContent = "0KTQsNC50Lsg0LfQsNCz0YDRg9C20LXQvQ==";
        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 25, "file-1", encodeContent, user1);
        cloudFileListTest.add(file1);

        Mockito.when(cloudService.getFile(filename, principal)).thenReturn(Base64.getDecoder().decode(file1.getFile()));

        // when
        ResponseEntity<byte[]> response = sut.getFile(filename, principal);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getFileTest_error400() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorInputData("ErrorInputData: файл не найден", 1))
                .when(cloudService).getFile(filename, principal);

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.getFile(filename, principal);
        });
        Assertions.assertEquals("ErrorInputData: файл не найден", exception.getMessage());
    }

    @Test
    public void getFileTest_error500() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorUploadFile("ErrorUploadFile: неизвестная ошибка загрузки файла", 1))
                .when(cloudService).getFile(filename, principal);

        // when

        // then
        ErrorUploadFile exception = Assertions.assertThrows(ErrorUploadFile.class, () -> {
            sut.getFile(filename, principal);
        });
        Assertions.assertEquals("ErrorUploadFile: неизвестная ошибка загрузки файла", exception.getMessage());
    }

    @Test
    public void editFileNameTest_200_OK() {
        // given
        FileName newName = new FileName("newName");
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doNothing().when(cloudService).editFileName(newName, filename, principal);

        // when
        ResponseEntity<Void> response = sut.editFileName(newName, filename, principal);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("<200 OK OK,[]>", response.toString());
    }

    @Test
    public void editFileNameTest_error400_1() {
        // given
        FileName newName = new FileName("file2");
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorInputData("ErrorInputData: Файл с таким именем уже существует", 1))
                .when(cloudService).editFileName(newName, filename, principal);

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.editFileName(newName, filename, principal);
        });
        Assertions.assertEquals("ErrorInputData: Файл с таким именем уже существует", exception.getMessage());
    }

    @Test
    public void editFileNameTest_error400_2() {
        // given
        FileName newName = new FileName("file2");
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorInputData("ErrorInputData: Выбранный файл не существует", 1))
                .when(cloudService).editFileName(newName, filename, principal);

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.editFileName(newName, filename, principal);
        });
        Assertions.assertEquals("ErrorInputData: Выбранный файл не существует", exception.getMessage());
    }

    @Test
    public void editFileNameTest_error500() {
        // given
        FileName newName = new FileName("file2");
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.doThrow(new ErrorEditFile("ErrorEditFile: Неизвестная ошибка редактирования файла", 1))
                .when(cloudService).editFileName(newName, filename, principal);

        // when

        // then
        ErrorEditFile exception = Assertions.assertThrows(ErrorEditFile.class, () -> {
            sut.editFileName(newName, filename, principal);
        });
        Assertions.assertEquals("ErrorEditFile: Неизвестная ошибка редактирования файла", exception.getMessage());
    }
}