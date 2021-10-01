package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class ProjectRequest {

    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String type;
    private String imageUrl;
    private String content;
    private List<TagRequest> tags;

    private ProjectRequest() {
    }

    public ProjectRequest(
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String type,
        String imageUrl,
        String content,
        List<TagRequest> tags
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

    public List<TagRequest> getTags() {
        return tags;
    }
}
