package com.woowacourse.pickgit.portfolio.application.dto.request;

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
    private List<TagRequestDto> tags;

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
        List<TagRequestDto> tags
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

    public List<TagRequestDto> getTags() {
        return tags;
    }
}
