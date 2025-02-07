package ru.yandex.praktikum.blog.repo.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import ru.yandex.praktikum.blog.config.DatabaseConfig;
import ru.yandex.praktikum.blog.model.PostWithDetails;
import ru.yandex.praktikum.blog.repo.DatabaseTest;
import ru.yandex.praktikum.blog.repo.post.JdbcNativePostRepository;
import ru.yandex.praktikum.blog.repo.post.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
        @ContextConfiguration(name = "repo", classes = {JdbcNativeTagRepository.class, JdbcNativePostRepository.class})
})
class JdbcNativeTagRepositoryTest extends DatabaseTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    void savePostTags() {
        PostWithDetails post = postRepository.findPostWithDetailsById(1).orElse(null);
        assertNotNull(post);
        assertEquals(List.of("тег1", "тег2"), post.getTags());
        tagRepository.savePostTags(1, List.of("тег3", "тег4"));
        post = postRepository.findPostWithDetailsById(1).orElse(null);
        assertNotNull(post);
        assertEquals(List.of("тег1", "тег2", "тег3", "тег4"), post.getTags());
    }

    @Test
    void deletePostTag() {
        PostWithDetails post = postRepository.findPostWithDetailsById(1).orElse(null);
        assertNotNull(post);
        assertEquals(List.of("тег1", "тег2"), post.getTags());
        tagRepository.deletePostTag(1, "тег1");
        post = postRepository.findPostWithDetailsById(1).orElse(null);
        assertNotNull(post);
        assertEquals(List.of("тег2"), post.getTags());
    }
}
