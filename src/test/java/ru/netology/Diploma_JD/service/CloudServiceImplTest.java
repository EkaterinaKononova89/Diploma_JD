package ru.netology.Diploma_JD.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.Diploma_JD.dto.CloudFileDto;
import ru.netology.Diploma_JD.dto.FileName;
import ru.netology.Diploma_JD.dto.Role;
import ru.netology.Diploma_JD.entity.CloudFile;
import ru.netology.Diploma_JD.entity.CloudUser;
import ru.netology.Diploma_JD.error.*;
import ru.netology.Diploma_JD.repository.CloudFileRepository;
import ru.netology.Diploma_JD.repository.CloudUserRepository;
import ru.netology.Diploma_JD.repository.ErrorRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CloudServiceImplTest {
    CloudServiceImpl sut;

    CloudUserRepository userRepository = Mockito.mock(CloudUserRepository.class);
    CloudFileRepository fileRepository = Mockito.mock(CloudFileRepository.class);
    ErrorRepository errorRepository = Mockito.mock(ErrorRepository.class);

    @BeforeEach
    public void beforeEach() {
        sut = new CloudServiceImpl(userRepository, fileRepository, errorRepository);
    }

    @AfterEach
    public void afterEach() {
        sut = null;
    }

    @Test
    public void getAllFilesTest() {
        // given
        int limit = 3;
        String userName = "user1";
        List<CloudFile> cloudFileListTest = new ArrayList<>();

        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 100, "file-1", "111", user1);
        CloudFile file2 = new CloudFile(2L, "file2", 200, "file-2", "222", user1);
        CloudFile file3 = new CloudFile(3L, "file3", 300, "file-3", "333", user1);
        CloudFile file4 = new CloudFile(4L, "file4", 400, "file-4", "444", user1);

        cloudFileListTest.add(file1);
        cloudFileListTest.add(file2);
        cloudFileListTest.add(file3);
        cloudFileListTest.add(file4);

        Mockito.when(fileRepository.findAllByUsername_UserName(Mockito.anyString())).thenReturn(Optional.of(cloudFileListTest));

        // when
        List<CloudFileDto> result = sut.getAllFiles(limit, userName);
        CloudFileDto expected1 = new CloudFileDto("file1", 100);
        CloudFileDto expected2 = new CloudFileDto("file2", 200);
        CloudFileDto expected3 = new CloudFileDto("file3", 300);

        // then
        Assertions.assertEquals(expected1, result.get(0));
        Assertions.assertEquals(expected2, result.get(1));
        Assertions.assertEquals(expected3, result.get(2));
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void getAllFilesTest_ErrorInputData() {
        // given
        int limit = 0;
        String userName = "user1";
        List<CloudFile> cloudFileListTest = new ArrayList<>();

        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 100, "file-1", "111", user1);
        CloudFile file2 = new CloudFile(2L, "file2", 200, "file-2", "222", user1);
        CloudFile file3 = new CloudFile(3L, "file3", 300, "file-3", "333", user1);
        CloudFile file4 = new CloudFile(4L, "file4", 400, "file-4", "444", user1);

        cloudFileListTest.add(file1);
        cloudFileListTest.add(file2);
        cloudFileListTest.add(file3);
        cloudFileListTest.add(file4);

        Mockito.when(fileRepository.findAllByUsername_UserName(Mockito.anyString())).thenReturn(Optional.of(cloudFileListTest));

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.getAllFiles(limit, userName);
        });

        Assertions.assertEquals("ErrorInputData: Размер отображаемого списка 0", exception.getMessage());
    }

    @Test
    public void getAllFilesTest_ErrorGettingFileList() {
        // given
        int limit = 3;
        String userName = "user1";

        Mockito.when(fileRepository.findAllByUsername_UserName(Mockito.anyString())).thenReturn(Optional.empty());

        // when

        // then
        ErrorGettingFileList exception = Assertions.assertThrows(ErrorGettingFileList.class, () -> {
            sut.getAllFiles(limit, userName);
        });

        Assertions.assertEquals("ErrorGettingFileList: Неизвестная ошибка получения списка файлов", exception.getMessage());
    }

    @Test
    public void deleteFileTest() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 100, "file-1", "111", user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));
        Mockito.doNothing().when(fileRepository).delete(file1);
        Mockito.when(fileRepository.existsById(file1.getId())).thenReturn(false);

        // when

        // then
        Assertions.assertDoesNotThrow(() -> {
            sut.deleteFile("file1", principal);
        });
    }

    @Test
    public void deleteFileTest_ErrorInputData() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";
        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName())).thenReturn(Optional.empty());

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.deleteFile("file1", principal);
        });

        Assertions.assertEquals("ErrorInputData: файл не найден", exception.getMessage());
    }

    @Test
    public void deleteFileTest_errorDeleteFile() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 100, "file-1", "111", user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));
        Mockito.doNothing().when(fileRepository).delete(file1);
        Mockito.when(fileRepository.existsById(file1.getId())).thenReturn(true);
        // when

        // then
        ErrorDeleteFile exception = Assertions.assertThrows(ErrorDeleteFile.class, () -> {
            sut.deleteFile("file1", principal);
        });

        Assertions.assertEquals("ErrorDeleteFile: неизвестная ошибка удаления файла", exception.getMessage());
    }

    @Test
    public void getFileTest() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        String encodeContent = "0KTQsNC50Lsg0LfQsNCz0YDRg9C20LXQvQ==";
        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 25, "file-1", encodeContent, user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));

        // when
        byte[] result = sut.getFile(filename, principal);

        //then
        Assertions.assertEquals(25, result.length);
    }

    @Test
    public void getFileTest_ErrorInputData() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName())).thenReturn(Optional.empty());

        // when

        //then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.getFile(filename, principal);
        });
        Assertions.assertEquals("ErrorInputData: файл не найден", exception.getMessage());
    }

    @Test
    public void getFileTest_ErrorUploadFile() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 100, "file-1", null, user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));

        // when

        //then
        ErrorUploadFile exception = Assertions.assertThrows(ErrorUploadFile.class, () -> {
            sut.getFile(filename, principal);
        });
        Assertions.assertEquals("ErrorUploadFile: неизвестная ошибка загрузки файла", exception.getMessage());
    }

    @Test
    public void editFileNameTest_error500() {
        // given
        FileName newName = new FileName("newName");
        String filename = "name";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "name", 100, "file-1", "111", user1);
        CloudFile fileRename = new CloudFile(1L, "newName", 100, "file-1", "111", user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));
        Mockito.when(fileRepository.save(file1)).thenReturn(fileRename); // на самом деле возвращается измененный file1 (а не new CloudFile)

        // when

        // then
        ErrorEditFile exception = Assertions.assertThrows(ErrorEditFile.class, () -> {
            sut.editFileName(newName, filename, principal);
        });
        Assertions.assertEquals("ErrorEditFile: Неизвестная ошибка редактирования файла", exception.getMessage());
    }

    @Test
    public void editFileNameTest_error400() {
        // given
        FileName newName = new FileName("name2");
        String filename = "name1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "name1", 100, "file-1", "111", user1);
        CloudFile file2 = new CloudFile(2L, "name2", 200, "file-2", "222", user1);
        cloudFileListTest.add(file1);
        cloudFileListTest.add(file2);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(newName.getFilename(), principal.getName()))
                .thenReturn(Optional.of(file2));

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            sut.editFileName(newName, filename, principal);
        });
        Assertions.assertEquals("ErrorInputData: Файл с таким именем уже существует", exception.getMessage());
    }
}
