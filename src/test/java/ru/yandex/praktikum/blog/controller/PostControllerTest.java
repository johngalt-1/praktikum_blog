package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.yandex.praktikum.blog.config.ThymeleafConfiguration;

import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ContextHierarchy({
        @ContextConfiguration(name = "web", classes = {ControllersTestConfig.class, ThymeleafConfiguration.class}),
        @ContextConfiguration(name = "controller", classes = PostController.class)
})
class PostControllerTest extends ControllerTest {

    @Test
    void getPost() throws Exception {
        when(postService.findPostWithDetailsById(anyLong())).thenReturn(mock());
        when(commentService.findCommentsByPostId(anyLong())).thenReturn(mock());
        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("comments"));
        verify(postService).findPostWithDetailsById(1L);
        verify(commentService).findCommentsByPostId(1L);
    }

    @Test
    void createPost() throws Exception {
        var title = "Заголовок";
        var text = "Текст";

        var imagesList = List.of("image100.png", "image200.jpg");
        MultiValueMap<String, String> images = new LinkedMultiValueMap<>();
        images.addAll("images", imagesList);

        var tag1 = "new_tag";
        var tag2 = "one_more_new_tag";
        var tagList = List.of(tag1, tag2);
        MultiValueMap<String, String> tags = new LinkedMultiValueMap<>();
        tags.addAll("tags", tagList);

        mockMvc.perform(
                        post("/post")
                                .param("title", title)
                                .param("text", text)
                                .params(images)
                                .params(tags)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(postService).createPost(title, text, imagesList, new HashSet<>(tagList));
    }

    @Test
    void updatePost() throws Exception {
        var title = "Заголовок";
        var text = "Текст";

        var imagesList = List.of("image100.png", "image200.jpg");
        MultiValueMap<String, String> images = new LinkedMultiValueMap<>();
        images.addAll("images", imagesList);

        var tag1 = "new_tag";
        var tag2 = "one_more_new_tag";
        var tagList = List.of(tag1, tag2);
        MultiValueMap<String, String> tags = new LinkedMultiValueMap<>();
        tags.addAll("tags", tagList);

        mockMvc.perform(
                        post("/post/1")
                                .param("title", title)
                                .param("text", text)
                                .params(images)
                                .params(tags)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/1"));

        verify(postService).updatePost(1, title, text, imagesList, new HashSet<>(tagList));
    }

    @Test
    void deletePost() throws Exception {
        mockMvc.perform(delete("/post/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(postService).deletePost(2);
    }

    @Test
    void likePost() throws Exception {
        mockMvc.perform(post("/post/3/like"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/3"));
        verify(postService).likePost(3);
    }
}
