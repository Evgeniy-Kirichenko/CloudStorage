package ru.netology.cloudstorage.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudstorage.dto.response.ExceptionRS;
import ru.netology.cloudstorage.exception.CloudFileException;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.UnauthorizedException;

@RestControllerAdvice
public class ExceptionControllerAdvance {
    @ExceptionHandler
    public ResponseEntity<ExceptionRS> handleInputData(InputDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionRS(e.getMessage(), 400));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRS> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionRS(e.getMessage(), 401));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRS> handleCloudFileException(CloudFileException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionRS(e.getMessage(), 500));
    }
}
