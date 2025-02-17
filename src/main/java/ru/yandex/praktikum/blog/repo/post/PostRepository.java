package ru.yandex.praktikum.blog.repo.post;

import org.springframework.lang.NonNull;
import ru.yandex.praktikum.blog.model.PostWithDetails;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    @NonNull
    List<PostWithDetails> findPostsWithDetails(int offset, int limit);

    @NonNull
    List<PostWithDetails> findPostsWithDetailsByTag(String tag, int offset, int limit);

    @NonNull
    Optional<PostWithDetails> findPostWithDetailsById(long id);

    long countPostsByTag(String tag);

    long countPosts();

    long savePost(String title, String text, List<String> images);

    void updatePost(long postId, String title, String text, List<String> images);

    void deletePost(long postId);
}
