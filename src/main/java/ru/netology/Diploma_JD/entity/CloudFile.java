package ru.netology.Diploma_JD.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor // для тестов
@Data
@Table(name = "FILES", schema = "netology_diploma")
public class CloudFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "file_size")
    private Integer size;

    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    @Column(name = "file_contents", length = 10000000)
    private String file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username_user")
    private CloudUser username;


    // переопределила equals и hashCode, но по факту в чистом виде hashCode в логике нигде не участвует
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudFile cloudFile)) return false;
        return Objects.equals(getFilename(), cloudFile.getFilename())
                && Objects.equals(getSize(), cloudFile.getSize())
                && Objects.equals(getFile(), cloudFile.getFile()) && Objects.equals(getUsername(), cloudFile.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilename(), getSize(), getFile(), getUsername());
    }
}