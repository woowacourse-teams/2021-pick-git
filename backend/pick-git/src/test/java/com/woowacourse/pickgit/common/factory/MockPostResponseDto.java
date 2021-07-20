package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public class MockPostResponseDto extends PostResponseDto {

    public MockPostResponseDto(
        Long id, List<String> imageUrls, String githubRepoUrl,
        String content, String authorName, String profileImageUrl, Integer likesCount,
        List<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt,
        List<CommentResponse> comments, Boolean isLiked
    ) {
        super(id, imageUrls, githubRepoUrl, content, authorName, profileImageUrl, likesCount, tags,
            createdAt, updatedAt, comments, isLiked);
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private List<String> imageUrls;
        private String githubRepoUrl;
        private String content;
        private String authorName;
        private String profileImageUrl;
        private Integer likesCount;
        private List<String> tags;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<CommentResponse> comments;
        private Boolean isLiked;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder imageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            return this;
        }

        public Builder imageUrls(String... imageUrls) {
            this.imageUrls = List.of(imageUrls);
            return this;
        }

        public Builder githubRepoUrl(String githubRepoUrl) {
            this.githubRepoUrl = githubRepoUrl;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public Builder profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public Builder likesCount(Integer likesCount) {
            this.likesCount = likesCount;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder tags(String... tags) {
            this.tags = List.of(tags);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder comments(List<CommentResponse> comments) {
            this.comments = comments;
            return this;
        }

        public Builder comments(CommentResponse... comments) {
            this.comments = List.of(comments);
            return this;
        }

        public Builder isLiked(Boolean isLiked) {
            this.isLiked = isLiked;
            return this;
        }

        public MockPostResponseDto build() {
            return new MockPostResponseDto(
                id, imageUrls, githubRepoUrl, content,
                authorName, profileImageUrl, likesCount, tags,
                createdAt, updatedAt, comments, isLiked
            );
        }
    }
}
