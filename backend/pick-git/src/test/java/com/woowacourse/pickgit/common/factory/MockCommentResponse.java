package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.post.application.dto.CommentResponse;

public class MockCommentResponse extends CommentResponse {

    public MockCommentResponse(Long id, String authorName, String content, Boolean isLiked) {
        super(id, authorName, content, isLiked);
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String authorName;
        private String content;
        private Boolean isLiked;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder isLiked(Boolean isLiked) {
            this.isLiked = isLiked;
            return this;
        }

        public MockCommentResponse build() {
            return new MockCommentResponse(id, authorName, content, isLiked);
        }
    }

}
