package ru.yandex.praktikum.blog.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@TestPropertySource(locations = "classpath:properties/application.properties")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FileManager.class)
class FileManagerTest {
    @Autowired
    FileManager fileManager;

    @BeforeEach
    void init() {
        fileManager.createImagesDirectory();
    }

    @AfterEach
    void clear() {
        fileManager.clearImagesDirectory();
    }

    @ParameterizedTest
    @CsvSource({
            "image.png,png",
            "image.jpg,jpg"
    })
    void saveFile(String originalFileName, String extension) throws IOException {
        var file = new MockMultipartFile("images", originalFileName, null, "a".getBytes());
        var fileName = fileManager.saveFile(file);
        assertTrue(fileName.endsWith(extension));
        var filePath = fileManager.getFilePath(fileName);
        assertEquals(Arrays.toString(file.getBytes()), Arrays.toString(Files.readAllBytes(filePath)));
    }

    @Test
    void validate() {
        var file1 = new MockMultipartFile("images", "name", null, "a".getBytes());
        var file2 = new MockMultipartFile("images", "name", null, "".getBytes());
        var file3 = new MockMultipartFile("images", "", null, "a".getBytes());
        var file4 = new MockMultipartFile("images", null, null, "a".getBytes());
        assertTrue(fileManager.validateFile(file1));
        assertFalse(fileManager.validateFile(file2));
        assertFalse(fileManager.validateFile(file3));
        assertFalse(fileManager.validateFile(file4));
    }
}