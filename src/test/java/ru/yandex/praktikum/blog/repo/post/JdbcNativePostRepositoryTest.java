package ru.yandex.praktikum.blog.repo.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import ru.yandex.praktikum.blog.DatabaseTest;
import ru.yandex.praktikum.blog.config.DatabaseConfig;
import ru.yandex.praktikum.blog.model.Post;
import ru.yandex.praktikum.blog.model.PostWithDetails;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
        @ContextConfiguration(name = "repo", classes = JdbcNativePostRepository.class)
})
class JdbcNativePostRepositoryTest extends DatabaseTest {

    @Autowired
    private PostRepository postRepository;

    private static PostWithDetails FIRST_POST;
    private static PostWithDetails SECOND_POST;
    private static PostWithDetails THIRD_POST;
    private static PostWithDetails DELETED_POST;

    @BeforeEach
    void initPosts() {
        FIRST_POST = new PostWithDetails(
                new Post(
                        1,
                        "Первый пост",
                        "текст",
                        List.of("image1.jpg", "image2.jpg"),
                        OffsetDateTime.of(2025, 2, 7, 13, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2025, 2, 7, 13, 0, 0, 0, ZoneOffset.UTC),
                        false
                ),
                2,
                2,
                Set.of("тег1", "тег2")
        );

        SECOND_POST = new PostWithDetails(
                new Post(
                        2,
                        "Второй пост",
                        "текст",
                        List.of("image3.jpg"),
                        OffsetDateTime.of(2025, 2, 7, 13, 1, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2025, 2, 7, 13, 2, 0, 0, ZoneOffset.UTC),
                        false
                ),
                0,
                0,
                Set.of("тег1")
        );

        THIRD_POST = new PostWithDetails(
                new Post(
                        3,
                        "Третий пост",
                        "текст",
                        List.of(),
                        OffsetDateTime.of(2025, 2, 7, 13, 3, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2025, 2, 7, 13, 5, 0, 0, ZoneOffset.UTC),
                        false
                ),
                0,
                1,
                Set.of()
        );

        DELETED_POST = new PostWithDetails(
                new Post(
                        4,
                        "Удалённый пост",
                        "текст",
                        List.of("image4.jpg"),
                        OffsetDateTime.of(2025, 2, 7, 12, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2025, 2, 7, 12, 1, 0, 0, ZoneOffset.UTC),
                        true
                ),
                1,
                1,
                Set.of("тег2")
        );
    }

    @Test
    void findPostsWithDetails() {
        var posts = postRepository.findPostsWithDetails(1, 2);
        assertEquals(2, posts.size());
        assertEqualPosts(SECOND_POST, posts.get(0));
        assertEqualPosts(FIRST_POST, posts.get(1));
    }

    @ParameterizedTest
    @MethodSource("postsByTag")
    void findPostsWithDetailsByTag(String tag, int offset, int limit, List<PostWithDetails> expectedPosts) {
        var posts = postRepository.findPostsWithDetailsByTag(tag, offset, limit);
        assertEquals(expectedPosts.size(), posts.size());
        for (int i = 0; i < expectedPosts.size(); i++) {
            PostWithDetails expected = expectedPosts.get(i);
            PostWithDetails actual = posts.get(i);
            assertEqualPosts(expected, actual);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void findPostWithDetailsById(int id) {
        PostWithDetails expected = switch (id) {
            case 1 -> FIRST_POST;
            case 2 -> SECOND_POST;
            case 3 -> THIRD_POST;
            case 4 -> DELETED_POST;
            default -> null;
        };
        var post = postRepository.findPostWithDetailsById(id);
        if (expected == null) {
            assertTrue(post.isEmpty());
        } else {
            assertTrue(post.isPresent());
            assertEqualPosts(expected, post.get());
        }
    }

    @Test
    void countPosts() {
        assertEquals(3, postRepository.countPosts());
    }

    @ParameterizedTest
    @CsvSource({
            "тег1,2",
            "тег2,1",
            "тег3,0",
    })
    void countPostsByTag(String tag, int count) {
        assertEquals(count, postRepository.countPostsByTag(tag));
    }

    @Test
    void savePost() {
        clear();

        var post = new Post(
                0,
                "Пятый пост",
                "текст",
                List.of("image5.jpg", "image6.png"),
                OffsetDateTime.of(2025, 2, 7, 16, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2025, 2, 7, 16, 5, 1, 0, ZoneOffset.UTC),
                false
        );
        long id = postRepository.savePost(post);
        post.setId(id);
        var foundPost = postRepository.findPostWithDetailsById(id).orElse(null);
        assertNotNull(foundPost);
        assertEqualPosts(post, foundPost.getPost());
    }

    @Test
    void updatePost() {
        var post = FIRST_POST.getPost();
        post.setText("новый текст");
        post.setTitle("Новый заголовок");
        post.setImages(List.of("image1.jpg", "image10.png"));
        post.setUpdateTime(OffsetDateTime.of(2025, 2, 7, 17, 22, 1, 0, ZoneOffset.UTC));
        postRepository.updatePost(post);
        var foundPost = postRepository.findPostWithDetailsById(FIRST_POST.getPost().getId()).orElse(null);
        assertNotNull(foundPost);
        assertEqualPosts(post, foundPost.getPost());
    }

    @Test
    void deletePost() {
        var post = FIRST_POST.getPost();
        post.setUpdateTime(OffsetDateTime.of(2025, 2, 7, 17, 22, 1, 0, ZoneOffset.UTC));
        postRepository.deletePost(post);
        post.setDeleted(true);
        var foundPost = postRepository.findPostWithDetailsById(FIRST_POST.getPost().getId()).orElse(null);
        assertNotNull(foundPost);
        assertEqualPosts(post, foundPost.getPost());
    }

    private void assertEqualPosts(PostWithDetails first, PostWithDetails second) {
        assertNotNull(first);
        assertNotNull(second);
        assertEqualPosts(first.getPost(), second.getPost());
        assertEquals(first.getCommentsCount(), second.getCommentsCount());
        assertEquals(first.getLikesCount(), second.getLikesCount());
        assertEquals(first.getTags(), second.getTags());
    }

    private void assertEqualPosts(Post first, Post second) {
        assertEquals(first.getId(), second.getId());
        assertEquals(first.getText(), second.getText());
        assertEquals(first.getTitle(), second.getTitle());
        assertEquals(first.getImages(), second.getImages());
        assertEquals(first.getCreationTime(), second.getCreationTime());
        assertEquals(first.getUpdateTime(), second.getUpdateTime());
        assertEquals(first.isDeleted(), second.isDeleted());
    }

    private static Stream<Arguments> postsByTag() {
        return Stream.of(
                Arguments.of("тег1", 0, 2, List.of(SECOND_POST, FIRST_POST)),
                Arguments.of("тег1", 1, 1, List.of(FIRST_POST)),
                Arguments.of("тег2", 0, 1, List.of(FIRST_POST)),
                Arguments.of("тег3", 0, 1, emptyList())
        );
    }
}