package com.woowacourse.pickgit.portfolio.application.dto.request;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class ProjectRequestDto {

    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String type;
    private String imageUrl;
    private String content;
    private List<String> tags;

    private ProjectRequestDto() {
    }

    public ProjectRequestDto(
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String type,
        String imageUrl,
        String content,
        List<String> tags
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

    public static ProjectRequestDto of(ProjectRequest request) {
        return ProjectRequestDto.builder()
            .id(request.getId())
            .name(request.getName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .type(request.getType())
            .imageUrl(request.getImageUrl())
            .content(request.getContent())
            .tags(request.getTags())
            .build();
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

    public List<String> getTags() {
        return tags;
    }
}
