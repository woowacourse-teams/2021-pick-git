package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class MockPostRequestDto extends PostRequestDto {

    public MockPostRequestDto(String token, String username,
        List<MultipartFile> images, String githubRepoUrl, List<String> tags, String content) {
        super(token, username, images, githubRepoUrl, tags, content);
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        private String token;
        private String username;
        private List<MultipartFile> images;
        private String githubRepoUrl;
        private List<String> tags;
        private String content;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder userName(String userName) {
            this.username = userName;
            return this;
        }

        public Builder images(List<MultipartFile> images) {
            this.images = images;
            return this;
        }

        public Builder images(MultipartFile... images) {
            this.images = List.of(images);
            return this;
        }

        public Builder githubRepoUrl(String githubRepoUrl) {
            this.githubRepoUrl = githubRepoUrl;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder tags(String... tags) {
            this.tags = List.of(tags);
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public MockPostRequestDto build() {
            return new MockPostRequestDto(token, username, images, githubRepoUrl, tags,
                content);
        }
    }
}
