package ru.netology.Diploma_JD.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.Diploma_JD.dto.CloudFileDto;
import ru.netology.Diploma_JD.dto.FileName;

import java.security.Principal;
import java.util.List;

public interface CloudController {
    ResponseEntity<List<CloudFileDto>> getAllFiles(@RequestParam int limit, Principal principal);

    ResponseEntity<Void> uploadFile(@RequestParam MultipartFile uploadFile, @RequestParam String filename, Principal principal);

    ResponseEntity<Void> deleteFile(@RequestParam String filename, Principal principal);

    ResponseEntity<byte[]> getFile(@RequestParam String filename, Principal principal);

    ResponseEntity<Void> editFileName(@RequestBody FileName newName, @RequestParam String filename, Principal principal);
}
