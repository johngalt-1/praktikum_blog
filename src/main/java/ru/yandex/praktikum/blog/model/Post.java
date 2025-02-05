package ru.yandex.praktikum.blog.model;

import java.time.OffsetDateTime;
import java.util.List;

public class Post {
    private long id;
    private String title;
    private String text;
    private List<String> images;
    private OffsetDateTime creationTime;
    private OffsetDateTime updateTime;
    private boolean deleted;

    public Post() {}

    public Post(
            long id,
            String title,
            String text,
            List<String> images,
            OffsetDateTime creationTime,
            OffsetDateTime updateTime,
            boolean deleted
    ) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.images = images;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    public OffsetDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public OffsetDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(OffsetDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
