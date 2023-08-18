package ru.netology.cloudstorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,reason = "Error input data")
public class CloudFileException extends RuntimeException{
    public CloudFileException(String message){
        super(message);
    }
}
