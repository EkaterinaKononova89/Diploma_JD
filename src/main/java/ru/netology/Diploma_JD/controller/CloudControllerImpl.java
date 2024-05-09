package ru.netology.Diploma_JD.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.Diploma_JD.dto.CloudFileDto;
import ru.netology.Diploma_JD.dto.FileName;
import ru.netology.Diploma_JD.handler.ExceptionController;
import ru.netology.Diploma_JD.service.CloudService;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/cloud")
public class CloudControllerImpl extends ExceptionController implements CloudController {
    private final CloudService cloudService; // завязан на интерфейс, а не конкретную реализацию

    @GetMapping(value = "/list")
    public ResponseEntity<List<CloudFileDto>> getAllFiles(@RequestParam(required = false) int limit, Principal principal) { // @RequestParam(required = false) - опечатка в спецификации? или действительно не обязательно?
        return new ResponseEntity<>(cloudService.getAllFiles(limit, principal.getName()), HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(@RequestParam(value = "file", required = false) MultipartFile uploadFile,
                                           @RequestParam String filename, Principal principal) {
        cloudService.uploadFile(uploadFile, filename, principal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestParam String filename, Principal principal) {
        cloudService.deleteFile(filename, principal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> getFile(@RequestParam(required = false) String filename, Principal principal) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(cloudService.getFile(filename, principal));
    }

    @PutMapping("/file")
    public ResponseEntity<Void> editFileName(@RequestBody FileName newName, @RequestParam(required = false) String filename, Principal principal) {
        cloudService.editFileName(newName, filename, principal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}