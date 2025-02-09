package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @ParameterizedTest
    @MethodSource("pageArgs")
    void feed(int pageSize, int pageNumber, List<Long> postIds) throws Exception {
        var url = "/?pageSize=" + pageSize + "&pageNumber=" + pageNumber;
        testUrl(url, postIds);
    }

    @Test
    void searchByTag() throws Exception {
        testUrl("/?pageSize=2&pageNumber=0&tag=тег1", List.of(2L, 1L));
        testUrl("/?pageSize=2&pageNumber=0&tag=тег2", List.of(1L));
    }

    private void testUrl(String url, List<Long> postIds) throws Exception {
        var result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"))
                .andReturn();
        assertPostIds(result, postIds);
    }

    private void assertPostIds(MvcResult result, List<Long> expectedPostIds) {
        var page = (Page<PostWithDetails>) result.getModelAndView().getModel().get("posts");
        var posts = page.getContent();
        var postIds = posts.stream().map(post -> post.getPost().getId()).toList();
        assertEquals(expectedPostIds, postIds);
    }

    private static Stream<Arguments> pageArgs() {
        return Stream.of(
                Arguments.of(1, 0, List.of(3L)),
                Arguments.of(1, 1, List.of(2L)),
                Arguments.of(1, 2, List.of(1L)),
                Arguments.of(2, 0, List.of(3L, 2L)),
                Arguments.of(2, 1, List.of(1L)),
                Arguments.of(3, 0, List.of(3L, 2L, 1L))
        );
    }
}
