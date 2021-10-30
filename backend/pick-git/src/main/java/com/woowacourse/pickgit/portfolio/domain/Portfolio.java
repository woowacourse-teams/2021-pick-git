package com.woowacourse.pickgit.portfolio.domain;

import com.woowacourse.pickgit.portfolio.domain.contact.Contacts;
import com.woowacourse.pickgit.portfolio.domain.project.Projects;
import com.woowacourse.pickgit.portfolio.domain.section.Sections;
import com.woowacourse.pickgit.user.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean profileImageShown;

    @Column(nullable = false)
    private String profileImageUrl;

    @Lob
    private String introduction;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Embedded
    private Contacts contacts;

    @Embedded
    private Projects projects;

    @Embedded
    private Sections sections;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Portfolio() {
    }

    public Portfolio(
        Long id,
        String name,
        Boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Contacts contacts,
        Projects projects,
        Sections sections
    ) {
        this(
            id,
            name,
            profileImageShown,
            profileImageUrl,
            introduction,
            createdAt,
            updatedAt,
            contacts,
            projects,
            sections,
            null
        );
    }

    public Portfolio(
        Long id,
        String name,
        Boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Contacts contacts,
        Projects projects,
        Sections sections,
        User user
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
        this.user = user;

        PortfolioValidator.username(name);
        PortfolioValidator.introduction(introduction);

        this.contacts.appendTo(this);
        this.projects.appendTo(this);
        this.sections.appendTo(this);
    }

    public static Portfolio empty(User user) {
        return new Portfolio(
            null,
            user.getName(),
            true,
            user.getImage(),
            Objects.isNull(user.getDescription()) ? "" : user.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            Contacts.empty(),
            Projects.empty(),
            Sections.empty(),
            user
        );
    }

    public void update(Portfolio portfolio) {
        this.name = portfolio.getName();
        this.profileImageShown = portfolio.profileImageShown;
        this.profileImageUrl = portfolio.profileImageUrl;
        this.introduction = portfolio.introduction;
        this.createdAt = portfolio.createdAt;
        this.updatedAt = portfolio.updatedAt;
        this.contacts.update(portfolio.contacts, this);
        this.projects.update(portfolio.projects, this);
        this.sections.update(portfolio.sections, this);
    }

    public boolean isOwnedBy(User user) {
        return this.user.equals(user);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getIntroduction() {
        return introduction;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public Projects getProjects() {
        return projects;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Portfolio)) {
            return false;
        }
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(id, portfolio.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
