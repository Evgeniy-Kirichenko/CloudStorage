package ru.netology.cloudstorage.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.request.EditFileNameRQ;
import ru.netology.cloudstorage.dto.response.FileRS;
import ru.netology.cloudstorage.service.CloudFileService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CloudFileController {
    CloudFileService cloudFileService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String fileName, MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        long size = file.getSize();
        byte[] bytes = file.getBytes();
        cloudFileService.uploadFile(authToken, fileName, contentType, size, bytes);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String fileName) {

        cloudFileService.deleteFile(authToken, fileName);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestHeader("auth-token") String authToken,
                                                 @RequestParam("filename") String fileName) {
        byte[] file = cloudFileService.downloadFile(authToken, fileName);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @GetMapping("/list")
    public List<FileRS> getAllFile(@RequestHeader("auth-token") String authToken,
                                   @RequestParam("limit") int limit) {
        return cloudFileService.getAllFileUser(authToken, limit).stream().limit(limit).map(o ->
                new FileRS(o.getFileName(), o.getSize())).collect(Collectors.toList());
    }

    @PutMapping("/file")
    public ResponseEntity<?> editFileName(@RequestHeader("auth-Token") String authToken,
                                          @RequestParam("filename") String fileName,
                                          @RequestBody EditFileNameRQ editFileNameRQ) {
        String newFileName = editFileNameRQ.getFileName();
        cloudFileService.editFileName(authToken, fileName, newFileName);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
