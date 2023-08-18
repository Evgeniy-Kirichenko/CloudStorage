package ru.netology.cloudstorage.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditFileNameRQ {
    String fileName;
@JsonCreator
    public EditFileNameRQ(String fileName) {
        this.fileName = fileName;
    }
}
