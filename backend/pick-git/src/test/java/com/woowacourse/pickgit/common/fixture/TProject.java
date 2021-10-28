package com.woowacourse.pickgit.common.fixture;

import static java.time.Month.OCTOBER;
import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.domain.project.ProjectType;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ProjectResponse;
import java.time.LocalDate;
import java.util.List;

public class TProject {
    
    private final ProjectResponse projectResponse;

    public TProject(ProjectResponse projectResponse) {
        this.projectResponse = projectResponse;
    }

    public static ProjectRequest of(TPost tPost) {
        List<TagRequest> tags = tPost.getTags().stream()
            .map(TagRequest::new)
            .collect(toList());

        return new ProjectRequest(
            null,
            tPost.name(),
            LocalDate.now(),
            LocalDate.now(),
            ProjectType.PERSONAL.getValue(),
            "test image url",
            tPost.getContent(),
            tags
        );
    }

    public static ProjectRequest invalidDateOf(TPost tPost) {
        List<TagRequest> tags = tPost.getTags().stream()
            .map(TagRequest::new)
            .collect(toList());

        return new ProjectRequest(
            null,
            tPost.name(),
            LocalDate.of(2021, OCTOBER, 26),
            LocalDate.of(2021, OCTOBER, 23),
            ProjectType.PERSONAL.getValue(),
            "test image url",
            tPost.getContent(),
            tags
        );
    }

    public Modifier modifier() {
        return new Modifier(projectResponse);
    }

    public static class Modifier {

        private final ProjectResponse projectResponse;

        private String name;
        private String type;
        private String imageUrl;
        private String content;
        private List<String> tags;

        public Modifier(ProjectResponse projectResponse) {
            this.projectResponse = projectResponse;
        }

        public Modifier name(String name) {
            this.name = name;
            return this;
        }

        public Modifier type(String type) {
            this.type = type;
            return this;
        }

        public Modifier imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Modifier content(String content) {
            this.content = content;
            return this;
        }

        public Modifier tags(String... tagNames) {
            this.tags = List.of(tagNames);
            return this;
        }

        public ProjectRequest build() {
            return ProjectRequest.builder()
                .id(projectResponse.getId())
                .name(name == null ? projectResponse.getName() : name)
                .type(name == null ? projectResponse.getType() : type)
                .imageUrl(imageUrl == null ? projectResponse.getImageUrl() : imageUrl)
                .content(content == null ? projectResponse.getContent() : content)
                .tags(tags == null ? toRequests(projectResponse.getTags()) : toRequests(tags))
                .build();
        }

        private List<TagRequest> toRequests(List<String> tags) {
            return tags.stream()
                .map(TagRequest::new)
                .collect(toList());
        }
    }
}
