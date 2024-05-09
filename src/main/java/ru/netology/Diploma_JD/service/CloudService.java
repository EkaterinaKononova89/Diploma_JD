package ru.netology.Diploma_JD.service;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.Diploma_JD.dto.CloudFileDto;
import ru.netology.Diploma_JD.dto.FileName;
import ru.netology.Diploma_JD.entity.CloudUser;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface CloudService {
    Optional<CloudUser> findByUserName(String username);

    List<CloudFileDto> getAllFiles(int limit, String name);

    void uploadFile(MultipartFile uploadFile, String filename, Principal principal);

    void deleteFile(String filename, Principal principal);

    byte[] getFile(String filename, Principal principal);

    void editFileName(FileName newName, String filename, Principal principal);
}
