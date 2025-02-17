package ru.yandex.praktikum.blog.repo.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import ru.yandex.praktikum.blog.DatabaseTest;
import ru.yandex.praktikum.blog.config.DatabaseConfig;
import ru.yandex.praktikum.blog.repo.post.JdbcNativePostRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
        @ContextConfiguration(name = "repo", classes = {JdbcNativeTagRepository.class, JdbcNativePostRepository.class})
})
class JdbcNativeTagRepositoryTest extends DatabaseTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    void findTagsByPostId() {
        var tags = tagRepository.findTagsByPostId(1);
        assertEquals(Set.of("тег1", "тег2"), tags);
    }

    @Test
    void savePostTags() {
        var tags = tagRepository.findTagsByPostId(1);
        assertEquals(Set.of("тег1", "тег2"), tags);
        tagRepository.savePostTags(1, Set.of("тег3", "тег4"));
        tags = tagRepository.findTagsByPostId(1);
        assertEquals(Set.of("тег1", "тег2", "тег3", "тег4"), tags);
    }

    @Test
    void deletePostTag() {
        var tags = tagRepository.findTagsByPostId(1);
        assertEquals(Set.of("тег1", "тег2"), tags);
        tagRepository.deletePostTag(1, "тег1");
        tags = tagRepository.findTagsByPostId(1);
        assertEquals(Set.of("тег2"), tags);
    }
}
