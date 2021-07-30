package com.woowacourse.pickgit.post.application.dto;

import com.woowacourse.pickgit.post.domain.comment.Comment;
import lombok.Builder;

@Builder
public class CommentResponse {

    private Long id;
    private String profileImageUrl;
    private String authorName;
    private String content;
    private Boolean liked;

    private CommentResponse() {
    }

    public CommentResponse(Long id, String profileImageUrl, String authorName, String content,
        Boolean liked) {
        this.id = id;
        this.profileImageUrl = profileImageUrl;
        this.authorName = authorName;
        this.content = content;
        this.liked = liked;
    }

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .profileImageUrl(comment.getProfileImageUrl())
            .authorName(comment.getAuthorName())
            .content(comment.getContent())
            .liked(false)
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

    public Boolean getLiked() {
        return liked;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
