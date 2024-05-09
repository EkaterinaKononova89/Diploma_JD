package ru.netology.Diploma_JD.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netology.Diploma_JD.dto.Role;

import java.util.List;

@Entity
@NoArgsConstructor // нужен для работы с БД
@AllArgsConstructor // для тестов
@Data
@Table(name = "USERS", schema = "netology_diploma")
public class CloudUser {
    @Id
    @Column(name = "username", length = 50, unique = true, nullable = false) // хоть unique и nullable вкл в @Id (primary key), дублирование не является избыточным
    private String userName;

    @Column(length = 70, nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, // Согласно стратегии FetchType.LAZY связанные объекты загружаются только по мере необходимости, т.е. при обращении
            mappedBy = "username") //Поле, которому принадлежит связь. Требуется, если связь не является однонаправленной.
    private List<CloudFile> file;
}