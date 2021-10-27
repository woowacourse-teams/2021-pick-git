package com.woowacourse.pickgit.portfolio.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class ProjectResponseDto {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private String imageUrl;
    private String content;
    private List<String> tags;

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
        List<String> tags
    ) {
        this(
            id,
            name,
            LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth()),
            LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth()),
            type,
            imageUrl,
            content,
            tags
        );
    }

    private ProjectResponseDto(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
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
