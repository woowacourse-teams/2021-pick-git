package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;

@Builder
public class PostDeleteRequestDto {

    private String username;
    private Long postId;

    private PostDeleteRequestDto() {
    }

    public PostDeleteRequestDto(AppUser user, Long postId) {
        this(user.getUsername(), postId);
    }

    public PostDeleteRequestDto(String username, Long postId) {
        this.username = username;
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public Long getPostId() {
        return postId;
    }
}
