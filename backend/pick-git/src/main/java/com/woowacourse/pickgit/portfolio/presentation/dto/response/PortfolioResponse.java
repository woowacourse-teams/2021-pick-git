package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class PortfolioResponse {

    private Long id;
    private String name;
    private boolean profileImageShown;
    private String profileImageUrl;
    private String introduction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ContactResponse> contacts;
    private List<ProjectResponse> projects;
    private List<SectionResponse> sections;

    private PortfolioResponse() {
    }

    public PortfolioResponse(
        Long id,
        String name,
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ContactResponse> contacts,
        List<ProjectResponse> projects,
        List<SectionResponse> sections
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

    public List<ContactResponse> getContacts() {
        return contacts;
    }

    public List<ProjectResponse> getProjects() {
        return projects;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
