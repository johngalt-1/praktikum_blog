package ru.yandex.praktikum.blog.service.post;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import ru.yandex.praktikum.blog.DatabaseTest;
import ru.yandex.praktikum.blog.config.DatabaseConfig;
import ru.yandex.praktikum.blog.repo.like.JdbcNativeLikeRepository;
import ru.yandex.praktikum.blog.repo.post.JdbcNativePostRepository;
import ru.yandex.praktikum.blog.repo.tag.JdbcNativeTagRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
        @ContextConfiguration(name = "service", classes = {
                PostService.class,
                JdbcNativePostRepository.class,
                JdbcNativeLikeRepository.class,
                JdbcNativeTagRepository.class
        })
})
class PostServiceTest extends DatabaseTest {

    @Autowired
    private PostService postService;

    @ParameterizedTest
    @MethodSource("pageArguments")
    void findPostsWithDetails(
            int pageSize,
            int pageNumber,
            int totalPages,
            int numberOfElements,
            boolean hasPrevious,
            boolean hasNext,
            List<Long> ids
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        var page = postService.findPostsWithDetails(pageable);

        assertEquals(pageSize, page.getSize());
        assertEquals(pageNumber, page.getNumber());
        assertEquals(3, page.getTotalElements());

        assertEquals(totalPages, page.getTotalPages());
        assertEquals(numberOfElements, page.getNumberOfElements());
        assertEquals(hasPrevious, page.hasPrevious());
        assertEquals(hasNext, page.hasNext());

        assertEquals(
                ids,
                page.getContent().stream().map(
                        postWithDetails -> postWithDetails.getPost().getId()
                ).toList()
        );
    }

    @Test
    void findPostsWithDetailsByTag() {
        Pageable pageable = PageRequest.of(1, 1);
        var page = postService.findPostsWithDetailsByTag("тег1", pageable);

        assertEquals(2, page.getTotalElements());

        assertEquals(2, page.getTotalPages());
        assertEquals(1, page.getNumberOfElements());
        assertTrue(page.hasPrevious());
        assertFalse(page.hasNext());

        assertEquals(
                1,
                page.getContent().getFirst().getPost().getId()
        );
    }

    @Test
    void createPost() {
        clear();

        var tags = Set.of("тег", "ещё_тег");
        var id = postService.createPost("Новый пост", "текст", List.of("image5.jpg", "image6.png"), tags);
        var foundPostWithDetails = postService.findPostWithDetailsById(id);

        assertTrue(foundPostWithDetails.isPresent());
        assertEquals(tags, foundPostWithDetails.get().getTags());
    }

    @Test
    void updatePost() {
        var postWithDetails = postService.findPostWithDetailsById(1);
        assertTrue(postWithDetails.isPresent());
        var post = postWithDetails.get().getPost();

        postService.updatePost(
                1,
                post.getTitle(),
                post.getText(),
                List.of("image1.jpg", "image3.png"),
                Set.of("тег2", "тег3")
        );
        postWithDetails = postService.findPostWithDetailsById(1);

        assertTrue(postWithDetails.isPresent());
        post = postWithDetails.get().getPost();
        assertEquals(Set.of("тег2", "тег3"), postWithDetails.get().getTags());
        assertEquals(List.of("image1.jpg", "image3.png"), post.getImages());
    }

    private static Stream<Arguments> pageArguments() {
        return Stream.of(
                Arguments.of(1, 0, 3, 1, false, true, List.of(3L)),
                Arguments.of(1, 1, 3, 1, true, true, List.of(2L)),
                Arguments.of(1, 2, 3, 1, true, false, List.of(1L)),
                Arguments.of(2, 0, 2, 2, false, true, List.of(3L, 2L)),
                Arguments.of(2, 1, 2, 1, true, false, List.of(1L)),
                Arguments.of(3, 0, 1, 3, false, false, List.of(3L, 2L, 1L))
        );
    }
}
