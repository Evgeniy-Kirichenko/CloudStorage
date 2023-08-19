package ru.netology.cloudstorage.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "files")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created", nullable = false, updatable = false)
    LocalDateTime localDateTime;

    @Column(name = "file_name")
    @NotBlank
    String fileName;

    @Column(name = "file_type", nullable = false)
    String fileType;

    @Column(nullable = false)
    long size;

    @Lob
    @Column(nullable = false)
    byte[] bytes;

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "username")
    User owner;

    public CloudFile(LocalDateTime localDateTime, String fileName, String fileType, long size, byte[] bytes, User owner) {
        this.localDateTime = localDateTime;
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.bytes = bytes;
        this.owner = owner;
    }
}
