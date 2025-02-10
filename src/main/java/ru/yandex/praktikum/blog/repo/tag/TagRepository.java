package ru.yandex.praktikum.blog.repo.tag;

import java.util.Set;

public interface TagRepository {
    Set<String> findTagsByPostId(long postId);

    void savePostTags(long postId, Set<String> tags);

    void deletePostTag(long postId, String tag);
}
