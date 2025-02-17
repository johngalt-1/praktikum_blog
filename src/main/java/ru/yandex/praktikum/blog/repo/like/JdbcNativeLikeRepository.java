package ru.yandex.praktikum.blog.repo.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcNativeLikeRepository implements LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void savePostLike(long postId) {
        jdbcTemplate.update(
                """
                        INSERT INTO blog.post_like (post_id, creation_time)
                        VALUES (?, now())
                        """,
                postId
        );
    }
}
