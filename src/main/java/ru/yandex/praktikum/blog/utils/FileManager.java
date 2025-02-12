package ru.yandex.praktikum.blog.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

@Component
public class FileManager {
    private final Path imagesPath;

    public FileManager(@Value("${images.dir}") String imagesDir) {
        this.imagesPath = Path.of(imagesDir);
    }

    public String saveFile(MultipartFile file) {
        var fileName = getFileName(file);
        var path = getFilePath(fileName);
        try {
            Files.copy(file.getInputStream(), path);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error while saving file", e);
        }
    }

    public Path getFilePath(String fileName) {
        return imagesPath.resolve(fileName).toAbsolutePath();
    }

    public boolean validateFile(MultipartFile file) {
        try {
            return file.getBytes().length > 0 &&
                   file.getOriginalFilename() != null &&
                   !file.getOriginalFilename().isBlank();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileName(MultipartFile file) {
        var timestamp = Instant.now().toEpochMilli();
        var extension = getFileExtension(file);
        return "image_" + timestamp + "." + extension;
    }

    private String getFileExtension(MultipartFile file) {
        var originalFileName = file.getOriginalFilename();
        return Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf(".") + 1);
    }
}
