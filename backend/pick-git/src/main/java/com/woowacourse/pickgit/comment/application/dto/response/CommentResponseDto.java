package com.woowacourse.pickgit.comment.application.dto.response;

import lombok.Builder;

@Builder
public class CommentResponseDto {

    private Long id;
    private String profileImageUrl;
    private String authorName;
    private String content;
    private Boolean liked;

    private CommentResponseDto() {
    }

    public CommentResponseDto(
        Long id,
        String profileImageUrl,
        String authorName,
        String content,
        Boolean liked
    ) {
        this.id = id;
        this.profileImageUrl = profileImageUrl;
        this.authorName = authorName;
        this.content = content;
        this.liked = liked;
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

    public Boolean getLiked() {
        return liked;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
