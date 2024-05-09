package ru.netology.Diploma_JD.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.Diploma_JD.entity.CloudUser;

import java.util.Optional;

@Repository
@Transactional
public interface CloudUserRepository extends CrudRepository<CloudUser, String> {
    Optional<CloudUser> findByUserName(String username);
}
