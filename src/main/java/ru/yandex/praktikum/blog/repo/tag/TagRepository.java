package ru.yandex.praktikum.blog.repo.tag;

import java.util.List;

public interface TagRepository {
    void savePostTags(long postId, List<String> tags);

    void deletePostTag(long postId, String tag);
}
