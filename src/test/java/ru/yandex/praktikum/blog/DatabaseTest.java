package ru.yandex.praktikum.blog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.praktikum.blog.config.DatabaseConfig;

@ExtendWith(SpringExtension.class)
@ContextHierarchy({
        @ContextConfiguration(name = "db", classes = DatabaseConfig.class),
})
@TestPropertySource(locations = "classpath:properties/application.properties")
public class DatabaseTest {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute(
                """
                        INSERT INTO blog.post (id, title, text, images, creation_time, update_time, deleted)
                        VALUES
                            (1, 'Первый пост', 'текст', '{"image1.jpg", "image2.jpg"}'::text[], '2025-02-07 13:00:00+00', '2025-02-07 13:00:00+00', false),
                            (2, 'Второй пост', 'текст', '{"image3.jpg"}'::text[], '2025-02-07 13:01:00+00', '2025-02-07 13:02:00+00', false),
                            (3, 'Третий пост', 'текст', '{}'::text[], '2025-02-07 13:03:00+00', '2025-02-07 13:05:00+00', false),
                            (4, 'Удалённый пост', 'текст', '{"image4.jpg"}'::text[], '2025-02-07 12:00:00+00', '2025-02-07 12:01:00+00', true)
                        """
        );
        jdbcTemplate.execute(
                """
                        INSERT INTO blog.comment (id, post_id, text, creation_time, update_time, deleted)
                        VALUES
                            (1, 1, 'Первый комментарий', '2025-02-07 13:10:00+00', '2025-02-07 13:10:00+00', false),
                            (2, 1, 'Второй комментарий', '2025-02-07 13:12:00+00', '2025-02-07 13:13:00+00', false),
                            (3, 1, 'Третий комментарий', '2025-02-07 13:11:00+00', '2025-02-07 13:11:30+00', true),
                            (4, 2, 'Первый комментарий', '2025-02-07 13:11:00+00', '2025-02-07 13:11:30+00', true),
                            (5, 4, 'Первый комментарий', '2025-02-07 13:00:00+00', '2025-02-07 13:00:00+00', false)
                        """
        );
        jdbcTemplate.execute(
                """
                        INSERT INTO blog.post_tag (post_id, tag)
                        VALUES
                            (1, 'тег1'),
                            (1, 'тег2'),
                            (2, 'тег1'),
                            (4, 'тег2')
                        """
        );
        jdbcTemplate.execute(
                """
                        INSERT INTO blog.post_like (post_id, creation_time)
                        VALUES
                            (1, '2025-02-07 14:00:00+00'),
                            (1, '2025-02-07 14:02:00+00'),
                            (3, '2025-02-07 14:00:00+00'),
                            (4, '2025-02-07 14:00:00+00')
                        """
        );
    }

    @AfterEach
    void clear() {
        jdbcTemplate.execute("DELETE FROM blog.comment");
        jdbcTemplate.execute("DELETE FROM blog.post_like");
        jdbcTemplate.execute("DELETE FROM blog.post_tag");
        jdbcTemplate.execute("DELETE FROM blog.post");
    }
}
