package ru.netology.cloudstorage;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.request.AuthenticationRQ;
import ru.netology.cloudstorage.dto.request.EditFileNameRQ;
import ru.netology.cloudstorage.dto.response.AuthenticationRS;
import ru.netology.cloudstorage.dto.response.FileRS;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.model.TokenUser;
import ru.netology.cloudstorage.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestData {
    public static final String USERNAME_1 = "Username1";
    public static final String PASSWORD_1 = "Password1";

    public static User USER_1 = new User(1L, USERNAME_1, PASSWORD_1, new ArrayList<>());
    public static TokenUser TOKEN_USER_1 = new TokenUser(1l, USERNAME_1, "Bearer Token");
    public static UserDetails USER_1_DET = new UserDetails() {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public String getPassword() {
            return PASSWORD_1;
        }

        @Override
        public String getUsername() {
            return USERNAME_1;
        }

        @Override
        public boolean isAccountNonExpired() {
            return false;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };
    public static final String TOKEN_1 = "Token1";
    public static final AuthenticationRQ AUTHENTICATION_RQ = new AuthenticationRQ(USERNAME_1, PASSWORD_1);
    public static final AuthenticationRS AUTHENTICATION_RS = new AuthenticationRS(TOKEN_1);
    public static final UsernamePasswordAuthenticationToken USERNAME_PASSWORD_AUTHENTICATION_TOKEN = new UsernamePasswordAuthenticationToken(USERNAME_1, PASSWORD_1);

    public static final String BEARER_TOKEN = "Bearer Token";
    public static final String BEARER_TOKEN_SPLIT = BEARER_TOKEN.split(" ")[1];
    public static final String USERNAME_2 = "Username2";
    public static final String PASSWORD_2 = "Password2";
    public static final String FILENAME_1 = "Filename1";
    public static final String FILENAME_2 = "Filename2";
    public static final byte[] FILE_CONTENT_2 = FILENAME_2.getBytes();
    public static final MultipartFile MULTIPART_FILE = new MockMultipartFile(FILENAME_2, FILE_CONTENT_2);

    public static final Long SIZE_1 = 100L;

    public static final byte[] FILE_CONTENT_1 = FILENAME_1.getBytes();
    public static final CloudFile STORAGE_FILE_1 = new CloudFile(1L, LocalDateTime.now(), FILENAME_1, null, SIZE_1, FILE_CONTENT_1, USER_1);
    public static final String NEW_FILENAME = "NewFilename";
    public static final EditFileNameRQ EDIT_FILE_NAME_RQ = new EditFileNameRQ(NEW_FILENAME);
    public static final Long SIZE_2 = 101L;
    public static final User USER_2 = new User(2L, USERNAME_2, PASSWORD_2, new ArrayList<>());
    public static final CloudFile STORAGE_FILE_2 = new CloudFile(2L, LocalDateTime.now(), FILENAME_2, null, SIZE_2, FILE_CONTENT_2, USER_2);
    public static final List<CloudFile> STORAGE_FILE_LIST = List.of(STORAGE_FILE_1, STORAGE_FILE_2);
    public static final FileRS FILE_RS_1 = new FileRS(FILENAME_1, SIZE_1);
    public static final FileRS FILE_RS_2 = new FileRS(FILENAME_2, SIZE_2);
    public static final List<CloudFile> FILE_RS_LIST = List.of(STORAGE_FILE_1, STORAGE_FILE_2);
    public static final Integer LIMIT = 100;
}
