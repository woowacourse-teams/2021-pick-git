package com.woowacourse.pickgit.portfolio.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class PortfolioResponseDto {

    private Long id;
    private String name;
    private boolean profileImageShown;
    private String profileImageUrl;
    private String introduction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ContactResponseDto> contacts;
    private List<ProjectResponseDto> projects;
    private List<SectionResponseDto> sections;

    private PortfolioResponseDto() {
    }

    public PortfolioResponseDto(
        Long id,
        String name,
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ContactResponseDto> contacts,
        List<ProjectResponseDto> projects,
        List<SectionResponseDto> sections
    ) {
        this.id = id;
        this.name = name;
        this.profileImageShown = profileImageShown;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.contacts = contacts;
        this.projects = projects;
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isProfileImageShown() {
        return profileImageShown;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<ContactResponseDto> getContacts() {
        return contacts;
    }

    public List<ProjectResponseDto> getProjects() {
        return projects;
    }

    public List<SectionResponseDto> getSections() {
        return sections;
    }
}
