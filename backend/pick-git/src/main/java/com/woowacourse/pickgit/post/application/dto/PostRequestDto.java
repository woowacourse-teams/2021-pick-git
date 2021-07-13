package com.woowacourse.pickgit.post.application.dto;

import java.util.List;

public class PostRequestDto {

    private String token;
    private String username;
    private List<String> images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private PostRequestDto() {
    }

    public PostRequestDto(String token, String username, List<String> images,
        String githubRepoUrl, List<String> tags, String content) {
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

    public List<String> getImages() {
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
