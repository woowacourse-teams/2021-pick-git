package com.woowacourse.pickgit.post.presentation.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class PostRequest {

    @NotEmpty
    private List<String> images;

    @NotBlank
    private String githubRepoUrl;

    private List<String> tags;

    @Size(max = 500, message = "F0001")
    private String content;

    private PostRequest() {
    }

    public PostRequest(
        List<String> images,
        String githubRepoUrl,
        List<String> tags,
        String content) {
        this.images = images;
        this.githubRepoUrl = githubRepoUrl;
        this.tags = tags;
        this.content = content;
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
