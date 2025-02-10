package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.praktikum.blog.config.ThymeleafConfiguration;
import ru.yandex.praktikum.blog.service.comment.CommentService;
import ru.yandex.praktikum.blog.service.post.PostService;

import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, ThymeleafConfiguration.class, PostController.class
})
class PostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


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

@Configuration
class TestConfig {

    @Bean
    public PostService postService() {
        return mock();
    }

    @Bean
    public CommentService commentService() {
        return mock();
    }
}
