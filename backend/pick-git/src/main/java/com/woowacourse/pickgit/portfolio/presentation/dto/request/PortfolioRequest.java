package com.woowacourse.pickgit.portfolio.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public class PortfolioRequest {

    private Long id;
    private boolean profileImageShown;
    private String profileImageUrl;
    private String introduction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ContactRequest> contacts;
    private List<ProjectRequest> projects;
    private List<SectionRequest> sections;

    private PortfolioRequest() {
    }

    public PortfolioRequest(
        Long id,
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ContactRequest> contacts,
        List<ProjectRequest> projects,
        List<SectionRequest> sections
    ) {
        this.id = id;
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

    public List<ContactRequest> getContacts() {
        return contacts;
    }

    public List<ProjectRequest> getProjects() {
        return projects;
    }

    public List<SectionRequest> getSections() {
        return sections;
    }
}
