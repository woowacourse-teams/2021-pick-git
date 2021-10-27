package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public class ProjectRequest {

    private Long id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String type;

    private String imageUrl;

    private String content;

    private List<TagRequest> tags;

    private ProjectRequest() {
    }

    public ProjectRequest(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
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
        return LocalDateTime.of(startDate, LocalTime.now());
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.of(endDate, LocalTime.now());
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
