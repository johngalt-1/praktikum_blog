package ru.yandex.praktikum.blog.controller;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CommentControllerTest extends ControllerTest {

    @Test
    void createComment() throws Exception {
        var text = """
                Я читаю текст
                
                Вы слушаете текст!
                """;

        mockMvc.perform(
                        post("/comment")
                                .param("text", text)
                                .param("postId", "4")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/4"));

        verify(commentService).saveComment(4, text);
    }

    @Test
    void updateComment() throws Exception {
        var text = "Текст";
        mockMvc.perform(
                        post("/comment/111")
                                .param("text", text)
                                .param("postId", "2")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/2"));
        verify(commentService).updateComment(111, text);
    }

    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/comment/222")).andExpect(status().isOk());
        verify(commentService).deleteComment(222);
    }
}