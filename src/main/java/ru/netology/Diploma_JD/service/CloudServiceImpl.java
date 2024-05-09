package ru.netology.Diploma_JD.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.Diploma_JD.dto.CloudFileDto;
import ru.netology.Diploma_JD.dto.FileName;
import ru.netology.Diploma_JD.entity.CloudFile;
import ru.netology.Diploma_JD.entity.CloudUser;
import ru.netology.Diploma_JD.error.*;
import ru.netology.Diploma_JD.repository.CloudFileRepository;
import ru.netology.Diploma_JD.repository.CloudUserRepository;
import ru.netology.Diploma_JD.repository.ErrorRepository;
import ru.netology.Diploma_JD.util.RenameFileUtil;

import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CloudServiceImpl implements CloudService, UserDetailsService {
    private final CloudUserRepository userRepository;
    private final CloudFileRepository fileRepository;
    private final ErrorRepository errorRepository;

    public Optional<CloudUser> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    @Override // привожу своего CloudUser'a к типу User SpringSecurity
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CloudUser cloudUser = findByUserName(username).orElseThrow(() -> new UsernameNotFoundException(
                "User with name " + username + "not found")); // эту ошибку не поймать? Всегда будет BadCredentials?
        return new User(
                cloudUser.getUserName(),
                cloudUser.getPassword(),
                List.of(new SimpleGrantedAuthority(cloudUser.getRole().toString()))
        );
    }

    public List<CloudFileDto> getAllFiles(int limit, String userName) {
        if (limit == 0) {
            throw new ErrorInputData("ErrorInputData: Размер отображаемого списка 0", errorRepository.setNumber());
        }
        List<CloudFile> cloudFileList = fileRepository.findAllByUsername_UserName(userName)
                .orElseThrow(() -> new ErrorGettingFileList("ErrorGettingFileList: Неизвестная ошибка получения списка файлов",
                        errorRepository.setNumber()));

        return cloudFileList.stream().map(file -> new CloudFileDto(file.getFilename(), file.getSize()))
                .limit(limit) // выводит только 3 файла из загруженных, к остальным доступа нет
                .collect(Collectors.toList());
    }

    public void uploadFile(MultipartFile uploadFile, String filename, Principal principal) {
        String filenameUnique;
        try {
            // здесь м.б. 400 ошибка
            filenameUnique = RenameFileUtil.checkUniqueName(fileRepository, errorRepository, uploadFile, filename, principal);

            byte[] fileContentArray = uploadFile.getBytes();

            String fileContent = Base64.getEncoder().encodeToString(fileContentArray); //в БД помещаю закодированный контент

            CloudFile cloudFile = new CloudFile();
            cloudFile.setFilename(filenameUnique);
            cloudFile.setSize((int) uploadFile.getSize());
            cloudFile.setUsername(userRepository.findByUserName(principal.getName()).get()); // Optional.get()' without 'isPresent()' check, т.к. пользователь вошел в свой аккаунт, значит он точно существует
            cloudFile.setFile(fileContent);

            cloudFile.setHash(Integer.toString(cloudFile.hashCode())); // cloudFile уже имеет все поля для генерации хеша

            fileRepository.save(cloudFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String filename, Principal principal) {
        CloudFile file = fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName())
                .orElseThrow(() -> new ErrorInputData("ErrorInputData: файл не найден", errorRepository.setNumber()));
        fileRepository.delete(file);

        if (fileRepository.existsById(file.getId())) {
            throw new ErrorDeleteFile("ErrorDeleteFile: неизвестная ошибка удаления файла", errorRepository.setNumber());
        }
    }

    public byte[] getFile(String filename, Principal principal) throws RuntimeException {
        CloudFile cloudFile = fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName())
                .orElseThrow(() -> new ErrorInputData("ErrorInputData: файл не найден", errorRepository.setNumber()));
        try {
            return Base64.getDecoder().decode(cloudFile.getFile());
        } catch (NullPointerException ex) {
            throw new ErrorUploadFile("ErrorUploadFile: неизвестная ошибка загрузки файла", errorRepository.setNumber());
        }
    }

    public void editFileName(FileName newName, String filename, Principal principal) {
        if (fileRepository.findCloudFileByFilenameAndUsername_UserName(newName.getFilename(), principal.getName()).isPresent()) {
        throw new ErrorInputData("ErrorInputData: Файл с таким именем уже существует", errorRepository.setNumber());
        }
        CloudFile cloudFile = fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName())
                .orElseThrow(() -> new ErrorInputData("ErrorInputData: Выбранный файл не существует", errorRepository.setNumber()));

        cloudFile.setFilename(newName.getFilename());
        fileRepository.save(cloudFile);
        if (fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()).isPresent()) {
            throw new ErrorEditFile("ErrorEditFile: Неизвестная ошибка редактирования файла", errorRepository.setNumber());
        }
    }
}