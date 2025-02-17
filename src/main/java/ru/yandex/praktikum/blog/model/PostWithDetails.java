package ru.yandex.praktikum.blog.model;

import java.util.Set;

public class PostWithDetails {
    private Post post;
    private long commentsCount;
    private long likesCount;
    private Set<String> tags;

    public PostWithDetails(Post post, long commentsCount, long likesCount, Set<String> tags) {
        this.post = post;
        this.commentsCount = commentsCount;
        this.likesCount = likesCount;
        this.tags = tags;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
