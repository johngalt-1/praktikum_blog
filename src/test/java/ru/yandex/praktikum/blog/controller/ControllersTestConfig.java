package ru.yandex.praktikum.blog.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.praktikum.blog.service.comment.CommentService;
import ru.yandex.praktikum.blog.service.post.PostService;
import ru.yandex.praktikum.blog.utils.FileManager;

import static org.mockito.Mockito.mock;

@Configuration
public class ControllersTestConfig {
    @Bean
    public PostService postService() {
        return mock();
    }

    @Bean
    public CommentService commentService() {
        return mock();
    }

    @Bean
    public FileManager fileManager() {
        return mock();
    }
}
