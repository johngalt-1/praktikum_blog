package ru.yandex.praktikum.blog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.praktikum.blog.model.PostWithDetails;
import ru.yandex.praktikum.blog.service.post.PostService;

@Controller
@RequestMapping("/")
public class FeedController {

    private final PostService postService;

    public FeedController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String feed(
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "tag", required = false) String tag,
            Model model
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PostWithDetails> posts;
        if (tag != null) {
            posts = postService.findPostsWithDetailsByTag(tag, pageable);
        } else {
            posts = postService.findPostsWithDetails(pageable);
        }
        model.addAttribute("posts", posts);
        return "feed";
    }
}
