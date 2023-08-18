package ru.netology.cloudstorage.dto.response;

import lombok.Data;

@Data
public class RegistrationUserDto {
 private String username;
 private String password;
 private String confirmPassword;
}
