package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.praktikum.blog.service.comment.CommentService;
import ru.yandex.praktikum.blog.service.post.PostService;
import ru.yandex.praktikum.blog.utils.FileManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@WebMvcTest
public class ControllerTest {

    @MockitoBean
    protected PostService postService;

    @MockitoBean
    protected CommentService commentService;

    @MockitoBean
    protected FileManager fileManager;

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    protected void setup() {
        when(fileManager.getFileUrl(any())).thenReturn("/");
    }

    @AfterEach
    protected void clear() {
        Mockito.reset(fileManager);
    }
}
