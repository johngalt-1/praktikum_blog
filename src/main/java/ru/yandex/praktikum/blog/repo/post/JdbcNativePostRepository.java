package ru.yandex.praktikum.blog.repo.post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.yandex.praktikum.blog.model.Post;
import ru.yandex.praktikum.blog.model.PostWithDetails;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.praktikum.blog.utils.Utils.fromSqlArray;
import static ru.yandex.praktikum.blog.utils.Utils.toSqlArray;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private static final String COMMENTS_COUNT_QUERY = """
            SELECT
                posts.id,
                COUNT(comments.id) AS comments_count
            FROM posts
            LEFT JOIN blog.comment AS comments ON posts.id = comments.post_id AND NOT comments.deleted
            GROUP BY posts.id
            """;

    private static final String LIKES_COUNT_QUERY = """
            SELECT
                posts.id,
                COUNT(likes.post_id) AS likes_count
            FROM posts
            LEFT JOIN blog.post_like AS likes ON posts.id = likes.post_id
            GROUP BY posts.id
            """;

    private static final String TAGS_QUERY = """
            WITH agg AS (
                SELECT
                    posts.id,
                    ARRAY_AGG(tag) AS tags
                FROM posts
                LEFT JOIN blog.post_tag AS tags ON posts.id = tags.post_id
                GROUP BY posts.id
            )
            SELECT
                id,
                CASE WHEN tags[1] IS NULL THEN '{}'::text[] ELSE tags END AS tags
            FROM agg
            """;

    private static final String FINAL_QUERY = """
            SELECT
                posts.id,
                title,
                text,
                images,
                creation_time,
                update_time,
                deleted,
                comments_count,
                likes_count,
                tags
            FROM posts
            JOIN comments_count ON posts.id = comments_count.id
            JOIN likes_count ON posts.id = likes_count.id
            JOIN tags ON posts.id = tags.id
            ORDER BY creation_time DESC
            """;

    private static String buildQuery(String postsQuery) {
        return String.format("""
                         WITH posts AS (
                         %s
                         ),
                         comments_count AS (
                             %s
                         ),
                         likes_count AS (
                             %s
                         ),
                         tags AS (
                             %s
                         )
                         %s
                        """,
                postsQuery,
                COMMENTS_COUNT_QUERY,
                LIKES_COUNT_QUERY,
                TAGS_QUERY,
                FINAL_QUERY
        );
    }

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @NonNull
    public List<PostWithDetails> findPostsWithDetails(int offset, int limit) {
        String query = buildQuery(
                String.format(
                        """
                                SELECT *
                                FROM blog.post
                                WHERE NOT deleted
                                ORDER BY creation_time DESC
                                LIMIT %d
                                OFFSET %d""",
                        limit,
                        offset
                )
        );
        return jdbcTemplate.query(query, (rs, rn) -> parseResultSet(rs));
    }

    @Override
    @NonNull
    public List<PostWithDetails> findPostsWithDetailsByTag(String tag, int offset, int limit) {
        String query = buildQuery(
                String.format(
                        """
                                SELECT post.*
                                FROM blog.post
                                JOIN blog.post_tag ON post.id = post_tag.post_id AND tag = '%s' AND NOT deleted
                                ORDER BY creation_time DESC
                                LIMIT %d
                                OFFSET %d
                                """,
                        tag,
                        limit,
                        offset
                )
        );
        return jdbcTemplate.query(query, (rs, rn) -> parseResultSet(rs));
    }

    @Override
    @NonNull
    public Optional<PostWithDetails> findPostWithDetailsById(long id) {
        String query = buildQuery(
                String.format(
                        """
                                SELECT *
                                FROM blog.post
                                WHERE id = %d
                                """,
                        id
                )
        );
        List<PostWithDetails> result = jdbcTemplate.query(query, (rs, rn) -> parseResultSet(rs));
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.getFirst());
        }
    }

    @Override
    public long countPosts() {
        String query = """
                SELECT COUNT(*)
                FROM blog.post
                WHERE NOT deleted
                """;
        return Objects.requireNonNull(jdbcTemplate.queryForObject(query, Long.class));
    }

    @Override
    public long countPostsByTag(String tag) {
        String query = String.format(
                """
                        SELECT COUNT(*)
                        FROM blog.post
                        JOIN blog.post_tag ON post.id = post_tag.post_id AND tag = '%s' AND NOT deleted
                        """,
                tag
        );
        return Objects.requireNonNull(jdbcTemplate.queryForObject(query, Long.class));
    }

    @Override
    public long savePost(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO blog.post (title, text, images, creation_time, update_time, deleted)
                            VALUES (?, ?, ?, ?, ?, false)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setArray(3, toSqlArray(post.getImages(), jdbcTemplate));
            ps.setObject(4, post.getCreationTime());
            ps.setObject(5, post.getUpdateTime());
            return ps;
        };
        jdbcTemplate.update(creator, keyHolder);
        var key = Objects.requireNonNull(keyHolder.getKeys()).get("id");
        return (long) key;
    }

    @Override
    public void updatePost(Post post) {
        jdbcTemplate.update(
                """
                        UPDATE blog.post
                        SET title = ?, text = ?, images = ?, update_time = ?
                        WHERE id = ?
                        """,
                ps -> {
                    ps.setString(1, post.getTitle());
                    ps.setString(2, post.getText());
                    ps.setArray(3, toSqlArray(post.getImages(), jdbcTemplate));
                    ps.setObject(4, post.getUpdateTime());
                    ps.setLong(5, post.getId());
                }
        );
    }

    @Override
    public void deletePost(Post post) {
        jdbcTemplate.update(
                """
                        UPDATE blog.post
                        SET update_time = ?, deleted = true
                        WHERE id = ?
                        """,
                ps -> {
                    ps.setObject(1, post.getUpdateTime());
                    ps.setLong(2, post.getId());
                }
        );
    }

    private PostWithDetails parseResultSet(ResultSet rs) throws SQLException {
        return new PostWithDetails(
                new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        fromSqlArray(rs, "images"),
                        rs.getObject("creation_time", OffsetDateTime.class),
                        rs.getObject("update_time", OffsetDateTime.class),
                        rs.getBoolean("deleted")
                ),
                rs.getLong("comments_count"),
                rs.getLong("likes_count"),
                new HashSet<>(fromSqlArray(rs, "tags"))
        );
    }
}
