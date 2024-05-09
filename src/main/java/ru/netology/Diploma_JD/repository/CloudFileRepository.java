package ru.netology.Diploma_JD.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.Diploma_JD.entity.CloudFile;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CloudFileRepository extends CrudRepository<CloudFile, Long> {
    Optional<CloudFile> findCloudFileByFilenameAndUsername_UserName(String filename, String username);

    Optional<List<CloudFile>> findAllByUsername_UserName(String username);
}
