package ru.netology.cloudstorage.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudstorage.dto.request.EditFileNameRQ;
import ru.netology.cloudstorage.dto.response.FileRS;
import ru.netology.cloudstorage.exception.CloudFileException;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.repository.TokenUserRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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
    public boolean uploadFile(String authToken, String fileName, String contentType, long size, byte[] bytes) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Добавление файла невозможно. Вы не авторизованны");
            throw new UnauthorizedException("Добавление файла невозможно. Вы не авторизованны");
        }
        if (size == 0) {
            log.error("файл не может быть пустым");
            throw new CloudFileException("Файл не может быть пустым");
        }

        cloudFileRepository.save(new CloudFile(LocalDateTime.now(), fileName, contentType,
                size, bytes, user));
        log.info("Файл успешно добавлен.Пользователь {}", user.getUsername());
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

    public List<CloudFile> getAllFileUser(String authToken, int limit) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Поиск файлов невозможен. Вы не авторизованны");
            throw new UnauthorizedException("Поиск файлов невозможен. Вы не авторизованны");
        }
        return cloudFileRepository.findAllByOwner(user);
    }

    @Transactional
    public void editFileName(String authToken, String fileName, String editFileName) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Переименование файла невозможно. Вы не авторизованны");
            throw new UnauthorizedException("Переименование файла невозможно. Вы не авторизованны");
        }
        CloudFile cloudFile = cloudFileRepository.findByOwnerAndFileName(user, fileName);
        cloudFile.setFileName(editFileName);
        cloudFileRepository.save(cloudFile);
        log.info("Файл {} успешно переименован в {}. Пользователь {}", fileName, editFileName, user.getUsername());
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

