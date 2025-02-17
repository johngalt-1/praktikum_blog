package ru.yandex.praktikum.blog.utils;

import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class FileManagerTestConfig {
    @Bean
    public ServletContext servletContext() {
        return mock();
    }
}
