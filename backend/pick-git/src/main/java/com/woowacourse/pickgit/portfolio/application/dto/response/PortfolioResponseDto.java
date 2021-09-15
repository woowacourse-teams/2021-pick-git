package com.woowacourse.pickgit.portfolio.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class PortfolioResponseDto {

    private Long id;
    private boolean profileImageShown;
    private String profileImageUrl;
    private String introduction;
    private List<ContactResponseDto> contacts;
    private List<ProjectResponseDto> projects;
    private List<SectionResponseDto> sections;

    private PortfolioResponseDto() {
    }

    public PortfolioResponseDto(
        Long id,
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        List<ContactResponseDto> contacts,
        List<ProjectResponseDto> projects,
        List<SectionResponseDto> sections
    ) {
        this.id = id;
        this.profileImageShown = profileImageShown;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.contacts = contacts;
        this.projects = projects;
        this.sections = sections;
    }

    public static PortfolioResponseDto from(
        Long id,
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        List<ContactResponseDto> contacts,
        List<ProjectResponseDto> projects,
        List<SectionResponseDto> sections
    ) {
        return PortfolioResponseDto.builder()
            .id(id)
            .profileImageShown(profileImageShown)
            .profileImageUrl(profileImageUrl)
            .introduction(introduction)
            .build();
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
