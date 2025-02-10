package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.praktikum.blog.DatabaseTest;
import ru.yandex.praktikum.blog.config.DatabaseConfig;
import ru.yandex.praktikum.blog.config.ThymeleafConfiguration;
import ru.yandex.praktikum.blog.config.WebConfig;
import ru.yandex.praktikum.blog.model.PostWithDetails;
import ru.yandex.praktikum.blog.repo.like.JdbcNativeLikeRepository;
import ru.yandex.praktikum.blog.repo.post.JdbcNativePostRepository;
import ru.yandex.praktikum.blog.repo.tag.JdbcNativeTagRepository;
import ru.yandex.praktikum.blog.service.post.PostService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
        @ContextConfiguration(name = "service", classes = {
                PostService.class,
                JdbcNativePostRepository.class,
                JdbcNativeLikeRepository.class,
                JdbcNativeTagRepository.class
        }),
        @ContextConfiguration(name = "web", classes = {
                WebConfig.class, ThymeleafConfiguration.class, FeedController.class
        }),
})
class FeedControllerTest extends DatabaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void feed() throws Exception {
        var url = "/?pageSize=3&pageNumber=0";
        testUrl(url);
    }

    @Test
    void searchByTag() throws Exception {
        testUrl("/?pageSize=2&pageNumber=0&tag=тег1");
        testUrl("/?pageSize=2&pageNumber=0&tag=тег2");
    }

    private void testUrl(String url) throws Exception {
        var result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andReturn();
        var postsPage = result.getModelAndView().getModel().get("posts");
        var posts = (Page<PostWithDetails>) postsPage;
        assertTrue(posts.hasContent());
    }
}
