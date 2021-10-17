package com.woowacourse.pickgit.post.presentation.dto.request;

import java.util.List;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public class PostRequest {

    private List<MultipartFile> images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private PostRequest() {
    }

    public PostRequest(
        List<MultipartFile> images,
        String githubRepoUrl,
        List<String> tags,
        String content
    ) {
        this.images = images;
        this.githubRepoUrl = githubRepoUrl;
        this.tags = tags;
        this.content = content;
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
