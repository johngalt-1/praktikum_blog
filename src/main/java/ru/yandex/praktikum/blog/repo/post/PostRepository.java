package ru.yandex.praktikum.blog.repo.post;

import org.springframework.lang.NonNull;
import ru.yandex.praktikum.blog.model.Post;
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

    long savePost(Post post);

    void updatePost(Post post);

    void deletePost(Post post);
}
