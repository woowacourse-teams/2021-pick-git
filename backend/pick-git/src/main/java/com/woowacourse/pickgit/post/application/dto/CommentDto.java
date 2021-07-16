package com.woowacourse.pickgit.post.application.dto;

import com.woowacourse.pickgit.post.domain.comment.Comment;

public class CommentDto {

    private Long id;
    private String authorName;
    private String content;
    private Boolean isLiked;

    private CommentDto() {
    }

    public CommentDto(Long id, String authorName, String content, Boolean isLiked) {
        this.id = id;
        this.authorName = authorName;
        this.content = content;
        this.isLiked = isLiked;
    }

    public static CommentDto from(Comment comment) {
        return new CommentDto(comment.getId(), comment.getAuthorName(), comment.getContent(), false);
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
}
