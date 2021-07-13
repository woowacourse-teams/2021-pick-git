package com.woowacourse.pickgit.post.application;

public class CommentResponseDto {

    private String userName;
    private String image;
    private String content;

    public CommentResponseDto(String userName, String image, String content) {
        this.userName = userName;
        this.image = image;
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }
}
