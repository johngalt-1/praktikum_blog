CREATE SCHEMA IF NOT EXISTS "blog";


-- Посты
CREATE TABLE IF NOT EXISTS "blog"."post" (
    "id" BIGSERIAL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "text" TEXT NOT NULL,
    "images" TEXT[],
    "creation_time" TIMESTAMPTZ NOT NULL,
    "update_time" TIMESTAMPTZ NOT NULL,
    "deleted" BOOLEAN NOT NULL
);
COMMENT ON TABLE "blog"."post" IS 'Посты';
COMMENT ON COLUMN "blog"."post"."title" IS 'Заголовок поста';
COMMENT ON COLUMN "blog"."post"."text" IS 'Текст поста';
COMMENT ON COLUMN "blog"."post"."images" IS 'Список ссылок на изображения в посте';
COMMENT ON COLUMN "blog"."post"."creation_time" IS 'Время создания поста';
COMMENT ON COLUMN "blog"."post"."update_time" IS 'Время обновления поста';
COMMENT ON COLUMN "blog"."post"."deleted" IS 'Удалён ли пост';

CREATE INDEX IF NOT EXISTS "i__post__creation_time"
ON "blog"."post" ("creation_time")
WHERE NOT "deleted";


-- Комментарии
CREATE TABLE IF NOT EXISTS "blog"."comment" (
    "id" BIGSERIAL PRIMARY KEY,
    "post_id" BIGINT NOT NULL REFERENCES "blog"."post" ("id"),
    "text" TEXT NOT NULL,
    "creation_time" TIMESTAMPTZ NOT NULL,
    "update_time" TIMESTAMPTZ NOT NULL,
    "deleted" BOOLEAN NOT NULL
);
COMMENT ON TABLE "blog"."comment" IS 'Комментарии к постам';
COMMENT ON COLUMN "blog"."comment"."post_id" IS 'ID поста, к которому оставлен комментарий';
COMMENT ON COLUMN "blog"."comment"."text" IS 'Текст комментария';
COMMENT ON COLUMN "blog"."comment"."creation_time" IS 'Время создания комментария';
COMMENT ON COLUMN "blog"."comment"."update_time" IS 'Время обновления комментария';
COMMENT ON COLUMN "blog"."comment"."deleted" IS 'Удалён ли комментарий';

CREATE INDEX IF NOT EXISTS "i__comment__post_id"
ON "blog"."comment" ("post_id", "creation_time")
WHERE NOT "deleted";


-- Лайки
CREATE TABLE IF NOT EXISTS "blog"."post_like" (
    "post_id" BIGINT NOT NULL REFERENCES "blog"."post" ("id"),
    "creation_time" TIMESTAMPTZ NOT NULL
);
COMMENT ON TABLE "blog"."post_like" IS 'Лайки к постам';
COMMENT ON COLUMN "blog"."post_like"."post_id" IS 'ID поста, которому поставлен лайк';
COMMENT ON COLUMN "blog"."post_like"."creation_time" IS 'Время простановки лайка';

CREATE INDEX IF NOT EXISTS "i__post_like__post_id"
ON "blog"."post_like" ("post_id");


-- Теги
CREATE TABLE IF NOT EXISTS "blog"."post_tag" (
    "post_id" BIGINT NOT NULL REFERENCES "blog"."post" ("id"),
    "tag" TEXT NOT NULL
);
COMMENT ON TABLE "blog"."post_tag" IS 'Теги постов';
COMMENT ON COLUMN "blog"."post_tag"."post_id" IS 'ID поста, в котором проставлен тег';
COMMENT ON COLUMN "blog"."post_tag"."tag" IS 'Тег';

CREATE UNIQUE INDEX IF NOT EXISTS "i__post_tag__tag__post_id__unique"
ON "blog"."post_tag" ("tag", "post_id");
