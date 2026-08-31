package com.example.KYC.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class DocumentController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<byte[]> getDocument(
            @PathVariable String fileName) throws IOException {

        // Laptop ke folder ka path
        Path directory = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        // Folder + database wala filename
        Path filePath = directory
                .resolve(fileName)
                .normalize();

        // Security: folder ke bahar ki file access na ho
        if (!filePath.getParent().equals(directory)) {
            return ResponseEntity.badRequest().build();
        }

        // File exist nahi karti
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        // File ko byte[] me read karo
        byte[] bytes = Files.readAllBytes(filePath);

        // Content type detect karo
        String lowerFileName = fileName.toLowerCase();

        MediaType mediaType;

        if (lowerFileName.endsWith(".png")) {

            mediaType = MediaType.IMAGE_PNG;

        } else if (
                lowerFileName.endsWith(".jpg")
                        || lowerFileName.endsWith(".jpeg")) {

            mediaType = MediaType.IMAGE_JPEG;

        } else if (lowerFileName.endsWith(".pdf")) {

            mediaType = MediaType.APPLICATION_PDF;

        } else {

            mediaType =
                    MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(bytes);
    }
}