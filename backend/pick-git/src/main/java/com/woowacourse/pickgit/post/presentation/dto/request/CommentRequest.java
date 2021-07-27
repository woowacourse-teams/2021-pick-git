package com.woowacourse.pickgit.post.presentation.dto.request;

import lombok.Builder;

@Builder
public class CommentRequest {

    private String userName;
    private String content;
    private Long postId;

    private CommentRequest() {
    }

    public CommentRequest(String userName, String content, Long postId) {
        this.userName = userName;
        this.content = content;
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public Long getPostId() {
        return postId;
    }
}
