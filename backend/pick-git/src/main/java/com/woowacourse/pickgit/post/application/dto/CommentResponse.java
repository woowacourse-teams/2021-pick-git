package com.woowacourse.pickgit.post.application.dto;

import com.woowacourse.pickgit.post.domain.comment.Comment;
import lombok.Builder;

@Builder
public class CommentResponse {

    private Long id;
    private String profileImageUrl;
    private String authorName;
    private String content;
    private Boolean isLiked;

    private CommentResponse() {
    }

    public CommentResponse(Long id, String profileImageUrl, String authorName, String content,
        Boolean isLiked) {
        this.id = id;
        this.profileImageUrl = profileImageUrl;
        this.authorName = authorName;
        this.content = content;
        this.isLiked = isLiked;
    }

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .profileImageUrl(comment.getProfileImageUrl())
            .authorName(comment.getAuthorName())
            .content(comment.getContent())
            .isLiked(false)
            .build();
    }

    public Long getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
