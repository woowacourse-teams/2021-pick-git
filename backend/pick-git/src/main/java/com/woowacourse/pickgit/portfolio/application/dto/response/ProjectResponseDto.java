package com.woowacourse.pickgit.portfolio.application.dto.response;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.domain.project.Project;
import com.woowacourse.pickgit.portfolio.domain.project.ProjectTag;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.TagRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class ProjectResponseDto {

    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String type;
    private String imageUrl;
    private String content;
    private List<TagResponseDto> tags;

    private ProjectResponseDto() {
    }

    public ProjectResponseDto(
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String type,
        String imageUrl,
        String content,
        List<TagResponseDto> tags
    ) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.imageUrl = imageUrl;
        this.content = content;
        this.tags = tags;
    }

    public static ProjectResponseDto of(ProjectRequest request) {
        return ProjectResponseDto.builder()
            .id(request.getId())
            .name(request.getName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .type(request.getType())
            .imageUrl(request.getImageUrl())
            .content(request.getContent())
            .tags(getTagResponsesDtoFromTagRequests(request.getTags()))
            .build();
    }

    private static List<TagResponseDto> getTagResponsesDtoFromTagRequests(
        List<TagRequest> requests
    ) {
        return requests.stream()
            .map(TagResponseDto::of)
            .collect(toList());
    }

    public static ProjectResponseDto of(Project project) {
        return ProjectResponseDto.builder()
            .id(project.getId())
            .name(project.getName())
            .startDate(project.getStartDate())
            .endDate(project.getEndDate())
            .type(project.getType().getValue())
            .imageUrl(project.getImageUrl())
            .content(project.getContent())
            .tags(getTagResponsesDtoFromProjectTags(project.getTags()))
            .build();
    }

    private static List<TagResponseDto> getTagResponsesDtoFromProjectTags(
        List<ProjectTag> tags
    ) {
        return tags.stream()
            .map(TagResponseDto::of)
            .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContent() {
        return content;
    }

    public List<TagResponseDto> getTags() {
        return tags;
    }
}
