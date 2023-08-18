package ru.netology.cloudstorage.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudFileService {
    CloudFileRepository cloudFileRepository;
    UserRepository userRepository;
    TokenUserRepository tokenUserRepository;

    @Transactional(rollbackFor = Exception.class)
    public boolean uploadFile(String authToken, String fileName, MultipartFile file) throws IOException {
        final User user = getUserByAuthToken(authToken);
        if (user == null) throw new UnauthorizedException("Добавление файла невозможно. Вы не авторизованны");
        if (file.isEmpty()) throw new CloudFileException("Файл не может быть пустым");
        CloudFile fileBuilder = CloudFile.builder().
                Id(1L).
                localDateTime(LocalDateTime.now()).
                fileName(fileName).
                fileType(file.getContentType()).
                size(file.getSize()).
                bytes(file.getBytes()).
                owner(user).
                build();
        cloudFileRepository.save(fileBuilder);
        return true;
    }

    @Transactional
    public void deleteFile(String authToken, String fileName) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) throw new UnauthorizedException("Добавление файла невозможно. Вы не авторизованны");
        cloudFileRepository.deleteByOwnerAndFileName(user, fileName);
    }

    public byte[] downloadFile(String authToken, String fileName) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) throw new UnauthorizedException("Поиск файла невозможен. Вы не авторизованны");
        final CloudFile file = cloudFileRepository.findByOwnerAndFileName(user, fileName);
        if (file == null) {
            throw new InputDataException("Ошибка загрузки файла.");
        }
        return file.getBytes();
    }

    public List<FileRS> getAllFileUser(String authToken, int limit) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) throw new UnauthorizedException("Поиск файлов невозможен. Вы не авторизованны");
        return cloudFileRepository.findAllByOwner(user).stream().limit(limit).map(o ->
                new FileRS(o.getFileName(), o.getSize())).collect(Collectors.toList());
    }

    @Transactional
    public void editFileName(String authToken, String fileName, EditFileNameRQ editFileNameRQ) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) throw new UnauthorizedException("Переименование файла невозможно. Вы не авторизованны");
        CloudFile cloudFile = cloudFileRepository.findByOwnerAndFileName(user, fileName);
        cloudFile.setFileName(editFileNameRQ.getFileName());
        cloudFileRepository.save(cloudFile);

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

