package ru.yandex.praktikum.blog.repo.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import ru.yandex.praktikum.blog.DatabaseTest;
import ru.yandex.praktikum.blog.config.DatabaseConfig;
import ru.yandex.praktikum.blog.model.Comment;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
        @ContextConfiguration(name = "repo", classes = JdbcNativeCommentRepository.class)
})
class JdbcNativeCommentRepositoryTest extends DatabaseTest {

    @Autowired
    private CommentRepository commentRepository;

    @ParameterizedTest
    @MethodSource("commentsByPostId")
    void findCommentsByPostId(long postId, List<Long> expectedCommentIds) {
        var commentIds = commentRepository.findCommentsByPostId(postId).stream().map(Comment::getId).toList();
        assertEquals(expectedCommentIds.size(), commentIds.size());
        for (int i = 0; i < expectedCommentIds.size(); i++) {
            long expected = expectedCommentIds.get(i);
            long actual = commentIds.get(i);
            assertEquals(expected, actual);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    void findCommentById(int id) {
        Comment expected = switch (id) {
            case 1 -> new Comment(
                    1,
                    1,
                    "Первый комментарий",
                    OffsetDateTime.of(2025, 2, 7, 13, 10, 0, 0, ZoneOffset.UTC),
                    OffsetDateTime.of(2025, 2, 7, 13, 10, 0, 0, ZoneOffset.UTC),
                    false
            );
            case 2 -> new Comment(
                    2,
                    1,
                    "Второй комментарий",
                    OffsetDateTime.of(2025, 2, 7, 13, 12, 0, 0, ZoneOffset.UTC),
                    OffsetDateTime.of(2025, 2, 7, 13, 13, 0, 0, ZoneOffset.UTC),
                    false
            );
            case 3 -> new Comment(
                    3,
                    1,
                    "Третий комментарий",
                    OffsetDateTime.of(2025, 2, 7, 13, 11, 0, 0, ZoneOffset.UTC),
                    OffsetDateTime.of(2025, 2, 7, 13, 11, 30, 0, ZoneOffset.UTC),
                    true
            );
            case 4 -> new Comment(
                    4,
                    2,
                    "Первый комментарий",
                    OffsetDateTime.of(2025, 2, 7, 13, 11, 0, 0, ZoneOffset.UTC),
                    OffsetDateTime.of(2025, 2, 7, 13, 11, 30, 0, ZoneOffset.UTC),
                    true
            );
            case 5 -> new Comment(
                    5,
                    4,
                    "Первый комментарий",
                    OffsetDateTime.of(2025, 2, 7, 13, 0, 0, 0, ZoneOffset.UTC),
                    OffsetDateTime.of(2025, 2, 7, 13, 0, 0, 0, ZoneOffset.UTC),
                    false
            );
            default -> null;
        };
        var comment = commentRepository.findCommentById(id);
        if (expected == null) {
            assertTrue(comment.isEmpty());
        } else {
            assertTrue(comment.isPresent());
            assertEqualComments(expected, comment.get());
        }
    }

    @Test
    void savePost() {
        jdbcTemplate.execute("DELETE FROM blog.comment");

        var comment = new Comment(
                0,
                3,
                "Новый коммент",
                OffsetDateTime.of(2025, 2, 7, 18, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2025, 2, 7, 18, 5, 0, 0, ZoneOffset.UTC),
                false
        );
        long id = commentRepository.saveComment(comment);
        comment.setId(id);
        var foundComment = commentRepository.findCommentById(id).orElse(null);
        assertNotNull(foundComment);
        assertEqualComments(comment, foundComment);
    }

    @Test
    void updatePost() {
        var comment = new Comment(
                1,
                1,
                "Первый комментарий с правками",
                OffsetDateTime.of(2025, 2, 7, 13, 10, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2025, 2, 7, 19, 10, 0, 0, ZoneOffset.UTC),
                false
        );
        commentRepository.updateComment(comment);
        var foundComment = commentRepository.findCommentById(1).orElse(null);
        assertNotNull(foundComment);
        assertEqualComments(comment, foundComment);
    }

    @Test
    void deletePost() {
        var comment = new Comment(
                1,
                1,
                "Первый комментарий",
                OffsetDateTime.of(2025, 2, 7, 13, 10, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2025, 2, 7, 19, 10, 0, 0, ZoneOffset.UTC),
                false
        );
        commentRepository.deleteComment(comment);
        comment.setDeleted(true);
        var foundComment = commentRepository.findCommentById(1).orElse(null);
        assertNotNull(foundComment);
        assertEqualComments(comment, foundComment);
    }

    private void assertEqualComments(Comment first, Comment second) {
        assertEquals(first.getId(), second.getId());
        assertEquals(first.getPostId(), second.getPostId());
        assertEquals(first.getText(), second.getText());
        assertEquals(first.getCreationTime(), second.getCreationTime());
        assertEquals(first.getUpdateTime(), second.getUpdateTime());
        assertEquals(first.isDeleted(), second.isDeleted());
    }

    private static Stream<Arguments> commentsByPostId() {
        return Stream.of(
                Arguments.of(1L, List.of(2L, 1L)),
                Arguments.of(2L, emptyList()),
                Arguments.of(3L, emptyList()),
                Arguments.of(4L, List.of(5L))
        );
    }
}
