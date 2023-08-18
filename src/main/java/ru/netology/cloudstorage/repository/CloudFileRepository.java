package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.model.User;

import java.util.List;

@Repository
@Transactional
public interface CloudFileRepository extends JpaRepository<CloudFile, Long> {
    void deleteByOwnerAndFileName(User user, String fileName);

    CloudFile findByOwnerAndFileName(User user, String fileName);

    List<CloudFile> findAllByOwner(User user);



}
