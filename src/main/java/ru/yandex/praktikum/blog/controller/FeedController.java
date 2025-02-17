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
import ru.yandex.praktikum.blog.utils.FileManager;

@Controller
@RequestMapping("/")
public class FeedController {

    private final PostService postService;
    private final FileManager fileManager;

    public FeedController(PostService postService, FileManager fileManager) {
        this.postService = postService;
        this.fileManager = fileManager;
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
            model.addAttribute("searchTag", tag);
        } else {
            posts = postService.findPostsWithDetails(pageable);
        }
        posts.getContent().forEach(postWithDetail -> {
            var post = postWithDetail.getPost();
            var images = post.getImages();
            var paths = images.stream()
                    .map(fileManager::getFileUrl)
                    .toList();
            post.setImages(paths);
        });
        model.addAttribute("posts", posts);
        return "feed";
    }
}
