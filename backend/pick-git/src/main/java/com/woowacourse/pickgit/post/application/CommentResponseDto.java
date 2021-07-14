package com.woowacourse.pickgit.post.application;

public class CommentResponseDto {

    private String authorName;
    private String content;
    private boolean isLiked;

    private CommentResponseDto() {
    }

    public CommentResponseDto(String authorName, String content) {
        this(authorName, content, false);
    }

    public CommentResponseDto(String authorName, String content, boolean isLiked) {
        this.authorName = authorName;
        this.content = content;
        this.isLiked = isLiked;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public boolean liked() {
        return isLiked;
    }
}

