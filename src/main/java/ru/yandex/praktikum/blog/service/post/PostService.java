package ru.yandex.praktikum.blog.service.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.yandex.praktikum.blog.model.Post;
import ru.yandex.praktikum.blog.model.PostWithDetails;
import ru.yandex.praktikum.blog.repo.like.LikeRepository;
import ru.yandex.praktikum.blog.repo.post.PostRepository;
import ru.yandex.praktikum.blog.repo.tag.TagRepository;

import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final TagRepository tagRepository;
    private final TransactionTemplate transactionTemplate;

    public PostService(
            PostRepository postRepository,
            LikeRepository likeRepository,
            TagRepository tagRepository,
            TransactionTemplate transactionTemplate
    ) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.tagRepository = tagRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public Page<PostWithDetails> findPostsWithDetails(Pageable pageable) {
        var posts = postRepository.findPostsWithDetails((int) pageable.getOffset(), pageable.getPageSize());
        var count = postRepository.countPosts();
        return new PageImpl<>(posts, pageable, count);
    }

    public Page<PostWithDetails> findPostsWithDetailsByTag(String tag, Pageable pageable) {
        var posts = postRepository.findPostsWithDetailsByTag(tag, (int) pageable.getOffset(), pageable.getPageSize());
        var count = postRepository.countPostsByTag(tag);
        return new PageImpl<>(posts, pageable, count);
    }

    public Optional<PostWithDetails> findPostWithDetailsById(long id) {
        return postRepository.findPostWithDetailsById(id);
    }

    public long savePost(Post post, List<String> tags) {
        var postId = transactionTemplate.execute(t -> {
            var id = postRepository.savePost(post);
            tagRepository.savePostTags(id, tags);
            return id;
        });
        return Objects.requireNonNull(postId);
    }

    public void updatePost(Post post, Set<String> tags) {
        transactionTemplate.executeWithoutResult(t -> {
            updateTags(post.getId(), tags);
            postRepository.updatePost(post);
        });
    }

    public void deletePost(Post post) {
        postRepository.deletePost(post);
    }

    public void likePost(Post post) {
        likeRepository.savePostLike(post.getId());
    }

    private void updateTags(long postId, Set<String> tags) {
        var oldTags = tagRepository.findTagsByPostId(postId);

        var tagsToAdd = new HashSet<>(tags);
        var tagsToDelete = new HashSet<>(oldTags);

        tagsToAdd.removeAll(tagsToDelete);
        tagsToDelete.removeAll(tags);

        tagRepository.savePostTags(postId, tagsToAdd.stream().toList());
        tagsToDelete.forEach(tag -> tagRepository.deletePostTag(postId, tag));
    }
}
