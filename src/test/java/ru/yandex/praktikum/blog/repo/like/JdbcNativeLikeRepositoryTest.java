package ru.yandex.praktikum.blog.repo.like;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import ru.yandex.praktikum.blog.DatabaseTest;
import ru.yandex.praktikum.blog.config.DatabaseConfig;
import ru.yandex.praktikum.blog.model.PostWithDetails;
import ru.yandex.praktikum.blog.repo.post.JdbcNativePostRepository;
import ru.yandex.praktikum.blog.repo.post.PostRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
        @ContextConfiguration(name = "repo", classes = {JdbcNativeLikeRepository.class, JdbcNativePostRepository.class})
})
class JdbcNativeLikeRepositoryTest extends DatabaseTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void savePostLike() {
        PostWithDetails post = postRepository.findPostWithDetailsById(1).orElse(null);
        assertNotNull(post);
        assertEquals(2, post.getLikesCount());
        likeRepository.savePostLike(1);
        post = postRepository.findPostWithDetailsById(1).orElse(null);
        assertNotNull(post);
        assertEquals(3, post.getLikesCount());
    }
}
