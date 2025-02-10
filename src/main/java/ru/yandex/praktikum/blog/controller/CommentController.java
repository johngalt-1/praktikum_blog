package ru.yandex.praktikum.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.praktikum.blog.service.comment.CommentService;

@Controller
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String createComment(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "postId") long postId
    ) {
        commentService.saveComment(postId, text);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/{commentId}")
    public String updateComment(
            @PathVariable(value = "commentId") long commentId,
            @RequestParam(value = "postId") long postId,
            @RequestParam(value = "text") String text
    ) {
        commentService.updateComment(commentId, text);
        return "redirect:/post/" + postId;
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(
            @PathVariable(value = "commentId") long commentId,
            @RequestParam(value = "postId") long postId
    ) {
        commentService.deleteComment(commentId);
        return "redirect:/post/" + postId;
    }
}
