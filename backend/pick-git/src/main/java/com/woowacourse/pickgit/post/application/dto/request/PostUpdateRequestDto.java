package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import java.util.List;
import lombok.Builder;

@Builder
public class PostUpdateRequestDto {

    private String username;
    private Long postId;
    private List<String> tags;
    private String content;

    private PostUpdateRequestDto() {
    }

    public PostUpdateRequestDto(AppUser user, Long postId, List<String> tags, String content) {
        this(user.getUsername(), postId, tags, content);
    }

    public PostUpdateRequestDto(String username, Long postId, List<String> tags, String content) {
        this.username = username;
        this.postId = postId;
        this.tags = tags;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public Long getPostId() {
        return postId;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }
}
