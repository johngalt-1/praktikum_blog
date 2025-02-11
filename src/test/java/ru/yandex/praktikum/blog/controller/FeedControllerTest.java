package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import ru.yandex.praktikum.blog.config.ThymeleafConfiguration;
import ru.yandex.praktikum.blog.model.Post;
import ru.yandex.praktikum.blog.model.PostWithDetails;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextHierarchy({
        @ContextConfiguration(name = "web", classes = {ControllersTestConfig.class, ThymeleafConfiguration.class}),
        @ContextConfiguration(name = "controller", classes = FeedController.class)
})
class FeedControllerTest extends ControllerTest {

    @Test
    void feed() throws Exception {
        var images1 = List.of("image1.jpg", "image2.png");
        var images2 = List.of("image3.png", "image4.jpg");
        Post post1 = mock();
        when(post1.getImages()).thenReturn(images1);
        Post post2 = mock();
        when(post2.getImages()).thenReturn(images2);
        PostWithDetails postWithDetails1 = mock();
        when(postWithDetails1.getPost()).thenReturn(post1);
        PostWithDetails postWithDetails2 = mock();
        when(postWithDetails2.getPost()).thenReturn(post2);
        when(postService.findPostsWithDetails(any())).thenReturn(
                new PageImpl<>(List.of(postWithDetails1, postWithDetails2))
        );
        when(fileManager.getFilePath(anyString())).thenReturn(Path.of("/foo/bar/"));

        testUrl("/?pageSize=3&pageNumber=0");

        verify(postService).findPostsWithDetails(any());
        verify(fileManager, times(4)).getFilePath(anyString());
        assertEquals(images1, post1.getImages());
        assertEquals(images2, post2.getImages());
    }

    @ParameterizedTest
    @ValueSource(strings = {"тег1", "тег2"})
    void searchByTag(String tag) throws Exception {
        when(postService.findPostsWithDetailsByTag(eq(tag), any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(fileManager.getFilePath(anyString())).thenReturn(Path.of("/foo/bar/"));

        testUrl("/?pageSize=2&pageNumber=0&tag=" + tag);

        verify(postService).findPostsWithDetailsByTag(eq(tag), any());
    }

    private void testUrl(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("feed"))
                .andExpect(model().attributeExists("posts"));
    }
}
