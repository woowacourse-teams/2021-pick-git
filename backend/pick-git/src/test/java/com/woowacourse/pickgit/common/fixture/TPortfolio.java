package com.woowacourse.pickgit.common.fixture;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ProjectRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ContactResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.PortfolioResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ProjectResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.SectionResponse;
import java.time.LocalDateTime;
import java.util.List;

public class TPortfolio {

    private final PortfolioResponse portfolioResponse;

    public TPortfolio(PortfolioResponse portfolioResponse) {
        this.portfolioResponse = portfolioResponse;
    }

    public Modifier modifier() {
        return new Modifier(portfolioResponse);
    }

    public static class Modifier {

        private final PortfolioResponse portfolioResponse;
        private Long id;
        private String name;
        private Boolean profileImageShown;
        private String profileImageUrl;
        private String introduction;
        private List<ContactRequest> contacts;
        private List<ProjectRequest> projects;
        private List<SectionRequest> sections;

        public Modifier(PortfolioResponse portfolioResponse) {
            this.portfolioResponse = portfolioResponse;
        }

        public Modifier id(Long id) {
            this.id = id;
            return this;
        }

        public Modifier name(String name) {
            this.name = name;
            return this;
        }

        public Modifier profileImageShown(boolean profileImageShown) {
            this.profileImageShown = profileImageShown;
            return this;
        }

        public Modifier profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public Modifier introduction(String introduction) {
            this.introduction = introduction;
            return this;
        }

        public Modifier contacts(List<ContactRequest> contacts) {
            this.contacts = contacts;
            return this;
        }

        public Modifier projects(List<ProjectRequest> projects) {
            this.projects = projects;
            return this;
        }

        public Modifier sections(List<SectionRequest> sections) {
            this.sections = sections;
            return this;
        }

        public PortfolioRequest build() {
            return PortfolioRequest.builder()
                .id(portfolioResponse.getId())
                .name(name == null ? portfolioResponse.getName() : name)
                .profileImageShown(profileImageShown == null ?
                    portfolioResponse.isProfileImageShown() : profileImageShown)
                .profileImageUrl(profileImageUrl == null ?
                    portfolioResponse.getProfileImageUrl() : profileImageUrl)
                .introduction(introduction == null ?
                    portfolioResponse.getIntroduction() : introduction)
                .contacts(contacts == null ?
                    toContactRequests(portfolioResponse.getContacts()) : contacts)
                .projects(projects == null ?
                    toProjectRequests(portfolioResponse.getProjects()) : projects)
                .sections(sections == null ?
                    toSectionRequests(portfolioResponse.getSections()) : sections)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        }

        private List<ContactRequest> toContactRequests(List<ContactResponse> contacts) {
            return contacts.stream()
                .map(contact -> new TContact(contact).modifier().build())
                .collect(toList());
        }

        private List<ProjectRequest> toProjectRequests(List<ProjectResponse> projects) {
            return projects.stream()
                .map(project -> new TProject(project).modifier().build())
                .collect(toList());
        }

        private List<SectionRequest> toSectionRequests(List<SectionResponse> sections) {
            return sections.stream()
                .map(section -> new TSection(section).modifier().build())
                .collect(toList());
        }
    }
}
