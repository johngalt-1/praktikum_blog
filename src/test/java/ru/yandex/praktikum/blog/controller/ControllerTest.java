package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.praktikum.blog.config.ThymeleafConfiguration;
import ru.yandex.praktikum.blog.service.comment.CommentService;
import ru.yandex.praktikum.blog.service.post.PostService;
import ru.yandex.praktikum.blog.utils.FileManager;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(name = "web", classes = {ControllersTestConfig.class, ThymeleafConfiguration.class}),
})
public class ControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected PostService postService;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected FileManager fileManager;

    protected MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    protected void clear() {
        Mockito.reset(fileManager);
    }
}
