package com.woowacourse.pickgit.portfolio.domain;

import com.woowacourse.pickgit.portfolio.domain.contact.Contacts;
import com.woowacourse.pickgit.portfolio.domain.project.Projects;
import com.woowacourse.pickgit.portfolio.domain.section.Sections;
import com.woowacourse.pickgit.user.domain.User;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean profileImageShown;

    @Column(nullable = false)
    private String profileImageUrl;

    private String introduction;

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
        Boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        Contacts contacts,
        Projects projects,
        Sections sections
    ) {
        this(
            id,
            profileImageShown,
            profileImageUrl,
            introduction,
            contacts,
            projects,
            sections,
            null
        );
    }

    public Portfolio(
        Long id,
        Boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        Contacts contacts,
        Projects projects,
        Sections sections,
        User user
    ) {
        this.id = id;
        this.profileImageShown = profileImageShown;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.contacts = contacts;
        this.projects = projects;
        this.sections = sections;
        this.user = user;

        this.contacts.appendTo(this);
        this.projects.appendTo(this);
        this.sections.appendTo(this);
    }

    public static Portfolio empty(User user) {
        return new Portfolio(
            null,
            true,
            user.getImage(),
            user.getDescription(),
            Contacts.empty(),
            Projects.empty(),
            Sections.empty(),
            user
        );
    }

    public void update(Portfolio portfolio) {
        this.profileImageShown = portfolio.profileImageShown;
        this.profileImageUrl = portfolio.profileImageUrl;
        this.introduction = portfolio.introduction;
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

    public boolean isProfileImageShown() {
        return profileImageShown;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
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
        if ((o instanceof Portfolio)) {
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
