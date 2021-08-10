package com.woowacourse.pickgit.post.presentation.dto.response;

import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class PostResponse {

    private Long id;
    private List<String> imageUrls;
    private String githubRepoUrl;
    private String content;
    private String authorName;
    private String profileImageUrl;
    private Integer likesCount;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDto> comments;
    private Boolean liked;

    private PostResponse() {
    }

    public PostResponse(
        Long id,
        List<String> imageUrls,
        String githubRepoUrl,
        String content,
        String authorName,
        String profileImageUrl,
        Integer likesCount,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentResponseDto> comments,
        Boolean liked
    ) {
        this.id = id;
        this.imageUrls = imageUrls;
        this.githubRepoUrl = githubRepoUrl;
        this.content = content;
        this.authorName = authorName;
        this.profileImageUrl = profileImageUrl;
        this.likesCount = likesCount;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.comments = comments;
        this.liked = liked;
    }

    public Long getId() {
        return id;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getGithubRepoUrl() {
        return githubRepoUrl;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public List<String> getTags() {
        return tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<CommentResponseDto> getComments() {
        return comments;
    }

    public Boolean getLiked() {
        return liked;
    }
}
