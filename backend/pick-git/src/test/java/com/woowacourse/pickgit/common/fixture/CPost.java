package com.woowacourse.pickgit.common.fixture;

import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.fixture.TPost.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CPost {
    private final Long id;
    private final String githubRepoUrl ;
    private final String content;
    private final List<String> tags;
    private final List<File> images;
    private final List<TUser> likes;
    private final List<Pair> comment;

    public CPost(Long id, String githubRepoUrl, String content, List<String> tags,
        List<File> images, List<TUser> likes,
        List<Pair> comment) {
        this.id = id;
        this.githubRepoUrl = githubRepoUrl;
        this.content = content;
        this.tags = tags;
        this.images = images;
        this.likes = likes;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public String getGithubRepoUrl() {
        return githubRepoUrl;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<File> getImages() {
        return images;
    }

    public List<TUser> getLikes() {
        return likes;
    }

    public List<Pair> getComment() {
        return comment;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id = null;
        private final String githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git";
        private String content ="Test Content";
        private List<String> tags = new ArrayList<>();
        private List<File> images = List.of(FileFactory.getTestImage1File());
        private List<TUser> likes = new ArrayList<>();
        private List<Pair> comment= new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder images(List<File> images) {
            this.images = images;
            return this;
        }

        public Builder likes(List<TUser> likes) {
            this.likes = likes;
            return this;
        }

        public CPost build() {
            return new CPost(
                id,
                githubRepoUrl,
                content,
                tags,
                images,
                likes,
                comment
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CPost cPost = (CPost) o;
        return Objects.equals(getId(), cPost.getId()) && Objects
            .equals(getGithubRepoUrl(), cPost.getGithubRepoUrl()) && Objects
            .equals(getContent(), cPost.getContent()) && Objects
            .equals(getTags(), cPost.getTags()) && Objects
            .equals(getImages(), cPost.getImages()) && Objects
            .equals(getLikes(), cPost.getLikes()) && Objects
            .equals(getComment(), cPost.getComment());
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(getId(), getGithubRepoUrl(), getContent(), getTags(), getImages(), getLikes(),
                getComment());
    }
}
