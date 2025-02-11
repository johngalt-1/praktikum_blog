package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.yandex.praktikum.blog.config.ThymeleafConfiguration;
import ru.yandex.praktikum.blog.model.Post;
import ru.yandex.praktikum.blog.model.PostWithDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        Post post = mock();
        when(post.getImages()).thenReturn(List.of("image1.jpg", "image2.png"));
        PostWithDetails postWithDetails = mock();
        when(postWithDetails.getPost()).thenReturn(post);
        when(postService.findPostWithDetailsById(anyLong())).thenReturn(Optional.of(postWithDetails));
        when(commentService.findCommentsByPostId(anyLong())).thenReturn(mock());
        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("images"));
        verify(postService).findPostWithDetailsById(1L);
        verify(commentService).findCommentsByPostId(1L);
        verify(fileManager, times(2)).getFilePath(anyString());
    }

    @Test
    void createPost() throws Exception {
        var title = "Заголовок";
        var text = "Текст";

        var tag1 = "new_tag";
        var tag2 = "one_more_new_tag";
        var tagList = List.of(tag1, tag2);
        MultiValueMap<String, String> tags = new LinkedMultiValueMap<>();
        tags.addAll("tags", tagList);

        when(fileManager.saveFile(any())).thenReturn("image_11212121.png").thenReturn("image_11212122.jpg");

        mockMvc.perform(
                        multipart("/post")
                                .file(new MockMultipartFile("images", null, null, (byte[]) null))
                                .file(new MockMultipartFile("images", null, null, (byte[]) null))
                                .param("title", title)
                                .param("text", text)
                                .params(tags)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));


        ArgumentCaptor<List<String>> captor = ArgumentCaptor.captor();
        verify(postService).createPost(eq(title), eq(text), captor.capture(), eq(new HashSet<>(tagList)));
        var images = captor.getValue();
        assertEquals(2, images.size());
        assertTrue(images.getFirst().endsWith(".png"));
        assertTrue(images.getLast().endsWith(".jpg"));
    }

    @Test
    void updatePost() throws Exception {
        var title = "Заголовок";
        var text = "Текст";

        var tag1 = "new_tag";
        var tag2 = "one_more_new_tag";
        var tagList = List.of(tag1, tag2);
        MultiValueMap<String, String> tags = new LinkedMultiValueMap<>();
        tags.addAll("tags", tagList);

        when(fileManager.saveFile(any())).thenReturn("image_11212121.png").thenReturn("image_11212122.jpg");

        mockMvc.perform(
                        multipart("/post/1")
                                .file(new MockMultipartFile("images", null, null, (byte[]) null))
                                .file(new MockMultipartFile("images", null, null, (byte[]) null))
                                .param("title", title)
                                .param("text", text)
                                .params(tags)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/1"));

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.captor();
        verify(postService).updatePost(eq(1L), eq(title), eq(text), captor.capture(), eq(new HashSet<>(tagList)));
        var images = captor.getValue();
        assertEquals(2, images.size());
        assertTrue(images.getFirst().endsWith(".png"));
        assertTrue(images.getLast().endsWith(".jpg"));
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
