package com.woowacourse.pickgit.comment.application.dto.request;

import lombok.Builder;

@Builder
public class CommentRequestDto {

    private String userName;
    private String content;
    private Long postId;

    private CommentRequestDto() {
    }

    public CommentRequestDto(String userName, String content, Long postId) {
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
