package ru.yandex.praktikum.blog.service.comment;

import org.springframework.stereotype.Service;
import ru.yandex.praktikum.blog.model.Comment;
import ru.yandex.praktikum.blog.repo.comment.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findCommentsByPostId(long postId) {
        return commentRepository.findCommentsByPostId(postId);
    }

    public Optional<Comment> findCommentById(long id) {
        return commentRepository.findCommentById(id);
    }

    public long saveComment(long postId, String text) {
        return commentRepository.saveComment(postId, text);
    }

    public void updateComment(long commentId, String text) {
        commentRepository.updateComment(commentId, text);
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteComment(commentId);
    }
}
