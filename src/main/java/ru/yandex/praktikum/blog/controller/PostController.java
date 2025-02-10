package ru.yandex.praktikum.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.praktikum.blog.service.comment.CommentService;
import ru.yandex.praktikum.blog.service.post.PostService;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable(value = "id") long postId, Model model) {
        var post = postService.findPostWithDetailsById(postId);
        var comments = commentService.findCommentsByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post";
    }

    @PostMapping
    public String createPost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "images") List<String> images,
            @RequestParam(value = "tags") Set<String> tags
    ) {
        postService.createPost(title, text, images, tags);
        return "redirect:/";
    }

    @PostMapping("/{id}")
    public String updatePost(
            @PathVariable(value = "id") long postId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "images") List<String> images,
            @RequestParam(value = "tags") Set<String> tags
    ) {
        postService.updatePost(postId, title, text, images, tags);
        return "redirect:/post/" + postId;
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable(value = "id") long postId) {
        postService.deletePost(postId);
        return "redirect:/";
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable(value = "id") long postId) {
        postService.likePost(postId);
        return "redirect:/post/" + postId;
    }
}
