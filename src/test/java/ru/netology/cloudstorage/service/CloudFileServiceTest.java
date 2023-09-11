package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.repository.TokenUserRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.TestData.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CloudFileServiceTest {
    @InjectMocks
    private CloudFileService cloudFileService;
    @Mock
    private CloudFileRepository cloudFileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenUserRepository tokenUserRepository;

    @BeforeEach
    void setUp() {
        Mockito.when(tokenUserRepository.findByAuthToken(BEARER_TOKEN_SPLIT)).thenReturn(Optional.ofNullable(TOKEN_USER_1));
        Mockito.when(userRepository.findByUsername(USERNAME_1)).thenReturn(Optional.ofNullable(USER_1));
    }

    @Test
    void uploadFile() throws IOException {
        assertTrue(cloudFileService.uploadFile(BEARER_TOKEN, FILENAME_1, MULTIPART_FILE.getContentType(), MULTIPART_FILE.getSize(), MULTIPART_FILE.getBytes()));
    }

    @Test
    void uploadFileUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> cloudFileService.uploadFile(TOKEN_1, FILENAME_1, MULTIPART_FILE.getContentType(), MULTIPART_FILE.getSize(), MULTIPART_FILE.getBytes()));
    }

    @Test
    void deleteFile() {
        cloudFileService.deleteFile(BEARER_TOKEN, FILENAME_1);
        Mockito.verify(cloudFileRepository, Mockito.times(1)).deleteByOwnerAndFileName(USER_1, FILENAME_1);
    }

    @Test
    void deleteFileUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> cloudFileService.deleteFile(TOKEN_1, FILENAME_1));
    }

    @Test
    void downloadFile() {
        Mockito.when(cloudFileRepository.findByOwnerAndFileName(USER_1, FILENAME_1)).thenReturn(STORAGE_FILE_1);
        assertEquals(FILE_CONTENT_1, cloudFileService.downloadFile(BEARER_TOKEN, FILENAME_1));
    }

    @Test
    void downloadFileUnauthorized() {
        Mockito.when(cloudFileRepository.findByOwnerAndFileName(USER_1, FILENAME_1)).thenReturn(STORAGE_FILE_1);
        assertThrows(UnauthorizedException.class, () -> cloudFileService.downloadFile(TOKEN_1, FILENAME_1));
    }

    @Test
    void getAllFiles() {
        Mockito.when(cloudFileRepository.findAllByOwner(USER_1)).thenReturn(STORAGE_FILE_LIST);
        assertEquals(FILE_RS_LIST, cloudFileService.getAllFileUser(BEARER_TOKEN, LIMIT));
    }

    @Test
    void editFileNameUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> cloudFileService.editFileName(TOKEN_1, FILENAME_1, NEW_FILENAME));
    }
}
