package ru.yandex.praktikum.blog.model;

import java.time.OffsetDateTime;

public class Comment {
    private long id;
    private long postId;
    private String text;
    private OffsetDateTime creationTime;
    private OffsetDateTime updateTime;
    private boolean deleted;

    public Comment() {}

    public Comment(
            long id,
            long postId,
            String text,
            OffsetDateTime creationTime,
            OffsetDateTime updateTime,
            boolean deleted
    ) {
        this.id = id;
        this.postId = postId;
        this.text = text;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(OffsetDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public OffsetDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
