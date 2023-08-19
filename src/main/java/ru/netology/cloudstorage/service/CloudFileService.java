package ru.netology.cloudstorage.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.request.EditFileNameRQ;
import ru.netology.cloudstorage.dto.response.FileRS;
import ru.netology.cloudstorage.exception.CloudFileException;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.model.TokenUser;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.repository.TokenUserRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudFileService {
    CloudFileRepository cloudFileRepository;
    UserRepository userRepository;
    TokenUserRepository tokenUserRepository;

    @Transactional(rollbackFor = Exception.class)
    public boolean uploadFile(String authToken, String fileName, MultipartFile file) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Добавление файла невозможно. Вы не авторизованны");
            throw new UnauthorizedException("Добавление файла невозможно. Вы не авторизованны");
        }
        if (file.isEmpty()) {
            log.error("файл не может быть пустым");
            throw new CloudFileException("Файл не может быть пустым");
        }

        try {
            cloudFileRepository.save(new CloudFile(LocalDateTime.now(), fileName, file.getContentType(),
                    file.getSize(), file.getBytes(), user));
            log.info("Файл успешно добавлен.Пользователь {}", user.getUsername());
        } catch (IOException e) {
            log.error("Ошибка загрузки файла.Пользователь {}", user.getUsername());
            throw new RuntimeException("Ошибка загрузки файла");
        }
        return true;
    }

    @Transactional
    public void deleteFile(String authToken, String fileName) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Удаление файла невозможно. Вы не авторизованны");
            throw new UnauthorizedException("Удаление файла невозможно. Вы не авторизованны");
        }
        cloudFileRepository.deleteByOwnerAndFileName(user, fileName);
        log.info("Файл {} успешно удален. Пользователь {}", fileName, user.getUsername());
    }

    public byte[] downloadFile(String authToken, String fileName) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Поиск файла невозможен. Вы не авторизованны");
            throw new UnauthorizedException("Поиск файла невозможен. Вы не авторизованны");
        }
        final CloudFile file = cloudFileRepository.findByOwnerAndFileName(user, fileName);
        if (file == null) {
            log.error("Ошибка загрузки файла{}. Пользователь {}", fileName, user.getUsername());
            throw new InputDataException("Ошибка загрузки файла");
        }
        log.info("Файл {} успешно выгружен. Пользователь {}", file, user.getUsername());
        return file.getBytes();
    }

    public List<FileRS> getAllFileUser(String authToken, int limit) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Поиск файлов невозможен. Вы не авторизованны");
            throw new UnauthorizedException("Поиск файлов невозможен. Вы не авторизованны");
        }
        return cloudFileRepository.findAllByOwner(user).stream().limit(limit).map(o ->
                new FileRS(o.getFileName(), o.getSize())).collect(Collectors.toList());
    }

    @Transactional
    public void editFileName(String authToken, String fileName, EditFileNameRQ editFileNameRQ) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Переименование файла невозможно. Вы не авторизованны");
            throw new UnauthorizedException("Переименование файла невозможно. Вы не авторизованны");
        }
        CloudFile cloudFile = cloudFileRepository.findByOwnerAndFileName(user, fileName);
        cloudFile.setFileName(editFileNameRQ.getFileName());
        cloudFileRepository.save(cloudFile);
        log.info("Файл {} успешно переименован в {}. Пользователь {}",fileName,editFileNameRQ.getFileName(),user.getUsername());
    }


    //ищем в репозитории tokenUserRepository по токену и возвращаем найденого пользователя из репозитория userRepository
    private User getUserByAuthToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = tokenUserRepository.findByAuthToken(authTokenWithoutBearer).get().getLogin();
            return userRepository.findByUsername(username).get();
        }
        return null;
    }


}

