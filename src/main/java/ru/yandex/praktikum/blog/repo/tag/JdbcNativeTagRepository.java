package ru.yandex.praktikum.blog.repo.tag;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcNativeTagRepository implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> findTagsByPostId(long postId) {
        var query = String.format(
                """
                        SELECT post_id, tag
                        FROM blog.post_tag
                        WHERE post_id = %d
                        """,
                postId
        );
        return jdbcTemplate.query(
                query,
                (rs, rn) -> rs.getString("tag")
        );
    }


    @Override
    public void savePostTags(long postId, List<String> tags) {
        jdbcTemplate.batchUpdate(
                """
                        INSERT INTO blog.post_tag (post_id, tag)
                        VALUES (?, ?)
                        """,
                tags.stream().map(tag -> new Object[]{postId, tag}).toList()
        );
    }

    @Override
    public void deletePostTag(long postId, String tag) {
        jdbcTemplate.update(
                """
                        DELETE FROM blog.post_tag
                        WHERE post_id = ? AND tag = ?
                        """,
                postId,
                tag
        );
    }
}
