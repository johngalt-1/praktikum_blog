package ru.yandex.praktikum.blog.repo.tag;

import java.util.List;

public interface TagRepository {
    List<String> findTagsByPostId(long postId);

    void savePostTags(long postId, List<String> tags);

    void deletePostTag(long postId, String tag);
}
