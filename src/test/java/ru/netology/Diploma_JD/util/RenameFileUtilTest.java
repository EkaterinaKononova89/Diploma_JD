package ru.netology.Diploma_JD.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.Diploma_JD.dto.Role;
import ru.netology.Diploma_JD.entity.CloudFile;
import ru.netology.Diploma_JD.entity.CloudUser;
import ru.netology.Diploma_JD.error.ErrorInputData;
import ru.netology.Diploma_JD.repository.CloudFileRepository;
import ru.netology.Diploma_JD.repository.ErrorRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RenameFileUtilTest {
    CloudFileRepository fileRepository = Mockito.mock(CloudFileRepository.class);

    @Test
    public void isFileExistInDbTest_true() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "name1", 100, "file-1", "111", user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));

        // when
        boolean result = RenameFileUtil.isFileExistInDb(fileRepository, filename, principal);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void isFileExistInDbTest_false() {
        // given
        String filename = "file1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "name1", 100, "file-1", "111", user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName())).thenReturn(Optional.empty());

        // when
        boolean result = RenameFileUtil.isFileExistInDb(fileRepository, filename, principal);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    public void isFilePossibleDuplicateTest_true() {
        // given
        MultipartFile uploadFile = Mockito.mock(MultipartFile.class);
        String filename = "file1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 257, "file-1", "111", user1);
        cloudFileListTest.add(file1);

        Mockito.when(uploadFile.getSize()).thenReturn(257L);
        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));

        // when
        boolean result = RenameFileUtil.isFilePossibleDuplicate(fileRepository, uploadFile, filename, principal);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void isFilePossibleDuplicateTest_false() {
        // given
        MultipartFile uploadFile = Mockito.mock(MultipartFile.class);
        String filename = "file1";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "file1", 300, "file-1", "111", user1);
        cloudFileListTest.add(file1); // по сути лишнее действие

        Mockito.when(uploadFile.getSize()).thenReturn(257L);
        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1));

        // when
        boolean result = RenameFileUtil.isFilePossibleDuplicate(fileRepository, uploadFile, filename, principal);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    public void stringIsNumberTest() {
        // given
        String possibleNumber1 = "123";
        String possibleNumber2 = "a4";
        String possibleNumber3 = "abc";

        // when
        boolean result1 = RenameFileUtil.stringIsNumber(possibleNumber1);
        boolean result2 = RenameFileUtil.stringIsNumber(possibleNumber2);
        boolean result3 = RenameFileUtil.stringIsNumber(possibleNumber3);

        // then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
        Assertions.assertFalse(result3);
    }

    public static Stream<Arguments> renameFileTest() {
        return Stream.of(
                Arguments.of("file.txt", "file (1).txt"),
                Arguments.of("file(1).txt", "file(1) (1).txt"),
                Arguments.of("file (1).txt", "file (2).txt"),
                Arguments.of("file (12).txt", "file (13).txt"),
                Arguments.of("file(1a).txt", "file(1a) (1).txt"),
                Arguments.of("file (1a).txt", "file (1a) (1).txt"),
                Arguments.of("file (1a) (2).txt", "file (1a) (3).txt")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void renameFileTest(String currentName, String newName) {
        // given

        // when
        String result = RenameFileUtil.renameFile(currentName);

        // then
        Assertions.assertEquals(newName, result);
    }

    @Test
    public void checkUniqueNameTest() {
        // given
        ErrorRepository errorRepository = Mockito.mock(ErrorRepository.class);
        MultipartFile uploadFile = Mockito.mock(MultipartFile.class);
        String filename = "currentFilename.png";
        Principal principal = () -> "user1";

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName())).thenReturn(Optional.empty()); // false for isFileExistInDb

        // when
        String result = RenameFileUtil.checkUniqueName(fileRepository, errorRepository, uploadFile, filename, principal);
        // then
        Assertions.assertEquals("currentFilename.png", result);
    }

    @Test
    public void checkUniqueNameTest_rename() {
        // given
        ErrorRepository errorRepository = Mockito.mock(ErrorRepository.class);
        MultipartFile uploadFile = Mockito.mock(MultipartFile.class);
        String filename = "currentFilename.png";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "currentFilename.png", 200, "file-1", "111", user1);
        cloudFileListTest.add(file1); // по сути лишнее действие

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1)); // true for isFileExistInDb
        Mockito.when(uploadFile.getSize()).thenReturn(257L); // false for isDuplicate

        // when
        String result = RenameFileUtil.checkUniqueName(fileRepository, errorRepository, uploadFile, filename, principal);
        // then
        Assertions.assertEquals("currentFilename (1).png", result);
    }

    @Test
    public void checkUniqueNameTest_ErrorInputData() {
        // given
        ErrorRepository errorRepository = Mockito.mock(ErrorRepository.class);
        MultipartFile uploadFile = Mockito.mock(MultipartFile.class);
        String filename = "currentFilename";
        Principal principal = () -> "user1";

        List<CloudFile> cloudFileListTest = new ArrayList<>();
        CloudUser user1 = new CloudUser("user1", "123", Role.ROLE_USER, cloudFileListTest);
        CloudFile file1 = new CloudFile(1L, "currentFilename", 257, "file-1", "111", user1);
        cloudFileListTest.add(file1);

        Mockito.when(fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()))
                .thenReturn(Optional.of(file1)); // true for isFileExistInDb
        Mockito.when(uploadFile.getSize()).thenReturn(257L); // true for isDuplicate

        // when

        // then
        ErrorInputData exception = Assertions.assertThrows(ErrorInputData.class, () -> {
            RenameFileUtil.checkUniqueName(fileRepository, errorRepository, uploadFile, filename, principal);
        });

        Assertions.assertEquals("ErrorInputData: Возможно это дубликат: файл с таким именем, расширением и " +
                "размером уже существует", exception.getMessage());
    }
}
