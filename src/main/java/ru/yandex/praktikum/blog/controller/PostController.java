package ru.yandex.praktikum.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.praktikum.blog.service.comment.CommentService;
import ru.yandex.praktikum.blog.service.post.PostService;
import ru.yandex.praktikum.blog.utils.FileManager;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final FileManager fileManager;

    public PostController(
            PostService postService,
            CommentService commentService,
            FileManager fileManager
    ) {
        this.postService = postService;
        this.commentService = commentService;
        this.fileManager = fileManager;
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable(value = "id") long postId, Model model) {
        var post = postService.findPostWithDetailsById(postId).orElseThrow(
                () -> new IllegalArgumentException("No post with such id")
        );
        var comments = commentService.findCommentsByPostId(postId);
        var images = post.getPost().getImages().stream().map(fileManager::getFileUrl).toList();
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("images", images);
        return "post";
    }

    @PostMapping
    public String createPost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "tags", required = false) Set<String> tags
    ) {
        tags = filterTags(tags);
        var imageNames = images.stream()
                .filter(fileManager::validateFile)
                .map(fileManager::saveFile)
                .toList();
        postService.createPost(title, text, imageNames, tags);
        return "redirect:/";
    }

    @PostMapping("/{id}")
    public String updatePost(
            @PathVariable(value = "id") long postId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "tags", required = false) Set<String> tags
    ) {
        tags = filterTags(tags);
        var imageNames = images.stream()
                .filter(fileManager::validateFile)
                .map(fileManager::saveFile)
                .toList();
        postService.updatePost(postId, title, text, imageNames, tags);
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

    private Set<String> filterTags(Set<String> tags) {
        if (tags == null) {
            return Collections.emptySet();
        }
        return tags.stream().filter(this::validateTag).collect(Collectors.toSet());
    }

    private boolean validateTag(String tag) {
        return tag != null && !tag.isBlank();
    }
}
