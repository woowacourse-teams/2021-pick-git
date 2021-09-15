package com.woowacourse.pickgit.portfolio.domain;

import com.woowacourse.pickgit.portfolio.domain.contact.Contact;
import com.woowacourse.pickgit.portfolio.domain.contact.Contacts;
import com.woowacourse.pickgit.portfolio.domain.project.Project;
import com.woowacourse.pickgit.portfolio.domain.project.Projects;
import com.woowacourse.pickgit.portfolio.domain.section.Section;
import com.woowacourse.pickgit.portfolio.domain.section.Sections;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
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
    private boolean profileImageShown;

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
        boolean profileImageShown,
        String profileImageUrl,
        String introduction,
        List<Contact> contacts,
        List<Project> projects,
        List<Section> sections,
        User user
    ) {
        this.id = id;
        this.profileImageShown = profileImageShown;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.contacts = new Contacts(contacts);
        this.projects = new Projects(projects);
        this.sections = new Sections(sections);
        this.user = user;
    }

    public boolean updateProfileImageShown(boolean profileImageShown) {
        this.profileImageShown = profileImageShown;
        return this.profileImageShown;
    }

    public String updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this.profileImageUrl;
    }

    public String updateIntroduction(String introduction) {
        this.introduction = introduction;
        return this.introduction;
    }

    public List<Contact> updateContacts(List<Contact> sources) {
        return contacts.updateContacts(sources);
    }

    public List<Project> updateProjects(List<Project> sources) {
        return projects.updateProjects(sources);
    }

    public List<Section> updateSections(List<Section> sources) {
        return sections.updateSections(sources);
    }

    public void addContact(Contact contact) {
        contact.linkPortfolio(this);
        contacts.add(contact);
    }

    public void removeContact(Contact contact) {
        contact.unlinkPortfolio(this);
        contacts.remove(contact);
    }

    public void addProject(Project project) {
        project.linkPortfolio(this);
        projects.add(project);
    }

    public void removeProject(Project project) {
        project.unlinkPortfolio(this);
        projects.remove(project);
    }

    public void addSection(Section section) {
        section.linkPortfolio(this);
        sections.add(section);
    }

    public void removeSection(Section section) {
        section.unlinkPortfolio(this);
        sections.remove(section);
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

    public User getUser() {
        return user;
    }
}
