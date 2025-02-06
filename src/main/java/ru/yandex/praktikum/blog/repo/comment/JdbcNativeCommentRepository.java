package ru.yandex.praktikum.blog.repo.comment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.yandex.praktikum.blog.model.Comment;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JdbcNativeCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @NonNull
    public List<Comment> findCommentsByPostId(long postId) {
        String query = String.format(
                """
                        SELECT
                            id,
                            post_id,
                            text,
                            creation_time,
                            update_time,
                            deleted
                        FROM blog.comment
                        WHERE post_id = %d AND NOT deleted
                        ORDER BY creation_time DESC
                        """,
                postId
        );
        return jdbcTemplate.query(
                query,
                (rs, rn) -> new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("text"),
                        rs.getObject("creation_time", OffsetDateTime.class),
                        rs.getObject("update_time", OffsetDateTime.class),
                        rs.getBoolean("deleted")
                )
        );
    }

    @Override
    @NonNull
    public Optional<Comment> findCommentById(long id) {
        String query = String.format(
                """
                        SELECT
                            id,
                            post_id,
                            text,
                            creation_time,
                            update_time,
                            deleted
                        FROM blog.comment
                        WHERE id = %d
                        """,
                id
        );
        List<Comment> result = jdbcTemplate.query(
                query,
                (rs, rn) -> new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("text"),
                        rs.getObject("creation_time", OffsetDateTime.class),
                        rs.getObject("update_time", OffsetDateTime.class),
                        rs.getBoolean("deleted")
                )
        );
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.getFirst());
        }
    }

    @Override
    public long saveComment(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO blog.comment (post_id, text, creation_time, update_time, deleted)
                            VALUES (?, ?, ?, ?, false)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, comment.getPostId());
            ps.setString(2, comment.getText());
            ps.setObject(3, comment.getCreationTime());
            ps.setObject(4, comment.getUpdateTime());
            return ps;
        };
        jdbcTemplate.update(creator, keyHolder);
        var key = Objects.requireNonNull(keyHolder.getKeys()).get("id");
        return (long) key;
    }

    @Override
    public void updateComment(Comment comment) {
        jdbcTemplate.update(
                """
                        UPDATE blog.comment
                        SET text = ?, update_time = ?
                        WHERE id = ?
                        """,
                ps -> {
                    ps.setString(1, comment.getText());
                    ps.setObject(2, comment.getUpdateTime());
                    ps.setLong(3, comment.getId());
                }
        );
    }

    @Override
    public void deleteComment(Comment comment) {
        jdbcTemplate.update(
                """
                        UPDATE blog.comment
                        SET update_time = ?, deleted = true
                        WHERE id = ?
                        """,
                ps -> {
                    ps.setObject(1, comment.getUpdateTime());
                    ps.setLong(2, comment.getId());
                }
        );
    }
}
