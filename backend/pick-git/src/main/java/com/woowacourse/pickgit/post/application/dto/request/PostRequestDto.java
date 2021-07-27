package com.woowacourse.pickgit.post.application.dto.request;

import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public class PostRequestDto {

    private String token;
    private String username;
    private List<MultipartFile> images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private PostRequestDto() {
    }

    public PostRequestDto(
        String token,
        String username,
        List<MultipartFile> images,
        String githubRepoUrl,
        List<String> tags,
        String content
    ) {
        this.token = token;
        this.username = username;
        this.images = images;
        this.githubRepoUrl = githubRepoUrl;
        this.tags = tags;
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public String getGithubRepoUrl() {
        return githubRepoUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }
}
