package com.woowacourse.pickgit.query.fixture;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.common.factory.FileFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum TPost {
    NEOZALPOST(
        "neozal post",
        List.of("java", "c++"),
        List.of(FileFactory.getTestImage1File(), FileFactory.getTestImage2File())
    ),
    MARKPOST(
        "mark post",
        List.of("java", "spring"),
        List.of(FileFactory.getTestImage1File())
    ),
    KEVINPOST(
        "kevin post",
        List.of("c++", "html"),
        List.of(FileFactory.getTestImage2File())
    ),
    UNKNOWN(
        Long.MAX_VALUE,
        "unkown post",
        List.of(),
        List.of()
    );

    private Long id;
    private final String githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git";
    private final String content;
    private final List<String> tags;
    private final List<File> images;
    private final List<TUser> likes;
    private final List<Pair> comment;

    TPost(String content, List<String> tags, List<File> images) {
        this(null, content, tags, images);
    }

    TPost(Long id, String content, List<String> tags, List<File> images) {
        this.id = id;
        this.content = content;
        this.likes = new ArrayList<>();
        this.tags = tags;
        this.images = images;
        this.comment = new ArrayList<>();
    }

    public static List<String> searchByTagAndGetContent(String keyword) {
        List<String> keys = List.of(keyword.split(" "));

        return Arrays.stream(values())
            .filter(post -> {
                for (String tag : post.tags) {
                    if (keys.contains(tag)) {
                        return true;
                    }
                }
                return false;
            })
            .map(post -> post.content)
            .collect(toList());
    }

    public static void clear() {
        for (TPost value : values()) {
            if(value.equals(UNKNOWN)) {
                continue;
            }

            value.clearValues();
        }
    }

    public Long getId() {
        if(id == null) {
            throw new IllegalStateException("아직 Post 생성이 안됨");
        }

        return id;
    }

    private void clearValues() {
        this.id = null;
        this.likes.clear();
        this.comment.clear();
    }

    protected void addLike(TUser tUser) {
        this.likes.add(tUser);
    }

    protected void addComment(TUser tUser, String comment) {
        this.comment.add(new Pair(tUser, comment));
    }

    protected List<Pair> getComment() {
        return comment;
    }

    protected List<TUser> getLikes() {
        return likes;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    protected Map<String, Object> params() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("githubRepoUrl", githubRepoUrl);
        map.put("tags", tags);
        map.put("content", content);

        return map;
    }

    protected List<File> images() {
        return images;
    }

    protected static final class Pair {
        TUser tUser;
        String comment;

        public Pair(TUser tUser, String comment) {
            this.tUser = tUser;
            this.comment = comment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Pair pair = (Pair) o;
            return tUser == pair.tUser && Objects.equals(comment, pair.comment);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tUser, comment);
        }
    }
}
