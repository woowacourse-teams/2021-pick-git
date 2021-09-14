package com.woowacourse.pickgit.portfolio.application.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public class PortfolioRequestDto {

    private boolean profileImageShown;
    private String profileImageUrl;
    private String introduction;
    private List<ContactRequestDto> contacts;
    private List<ProjectRequestDto> projects;
    private List<SectionRequestDto> sections;

    private PortfolioRequestDto() {
    }

    public PortfolioRequestDto(
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        List<ContactRequestDto> contacts,
        List<ProjectRequestDto> projects,
        List<SectionRequestDto> sections
    ) {
        this.profileImageShown = profileImageShown;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.contacts = contacts;
        this.projects = projects;
        this.sections = sections;
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
