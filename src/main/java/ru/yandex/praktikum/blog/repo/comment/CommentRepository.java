package ru.yandex.praktikum.blog.repo.comment;

import org.springframework.lang.NonNull;
import ru.yandex.praktikum.blog.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    @NonNull
    List<Comment> findCommentsByPostId(long postId);

    @NonNull
    Optional<Comment> findCommentById(long id);

    long saveComment(long postId, String text);

    void updateComment(long commentId, String text);

    void deleteComment(long commentId);
}
