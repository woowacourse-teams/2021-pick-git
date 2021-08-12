package com.woowacourse.pickgit.comment.application.dto.request;

import lombok.Builder;

@Builder
public class CommentDeleteRequestDto {

    private String username;
    private Long postId;
    private Long commentId;

    private CommentDeleteRequestDto() {
    }

    public CommentDeleteRequestDto(String username, Long postId, Long commentId) {
        this.username = username;
        this.postId = postId;
        this.commentId = commentId;
    }

    public String getUsername() {
        return username;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCommentId() {
        return commentId;
    }
}
