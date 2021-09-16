package com.woowacourse.pickgit.portfolio.presentation.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class PortfolioResponse {

    private Long id;
    private boolean profileImageShown;
    private String profileImageUrl;
    private String introduction;
    private List<ContactResponse> contacts;
    private List<ProjectResponse> projects;
    private List<SectionResponse> sections;

    private PortfolioResponse() {
    }

    public PortfolioResponse(
        Long id,
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        List<ContactResponse> contacts,
        List<ProjectResponse> projects,
        List<SectionResponse> sections
    ) {
        this.id = id;
        this.profileImageShown = profileImageShown;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
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
