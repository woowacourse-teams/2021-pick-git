package com.woowacourse.pickgit.portfolio.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class PortfolioRequestDto {

    private Long id;
    private String name;
    private boolean profileImageShown;
    private String profileImageUrl;
    private String introduction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ContactRequestDto> contacts;
    private List<ProjectRequestDto> projects;
    private List<SectionRequestDto> sections;

    private PortfolioRequestDto() {
    }

    public PortfolioRequestDto(
        Long id,
        String name,
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ContactRequestDto> contacts,
        List<ProjectRequestDto> projects,
        List<SectionRequestDto> sections
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

    public List<ContactRequestDto> getContacts() {
        return contacts;
    }

    public List<ProjectRequestDto> getProjects() {
        return projects;
    }

    public List<SectionRequestDto> getSections() {
        return sections;
    }
}
