package com.woowacourse.pickgit.portfolio.domain;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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

    @OneToMany(
        mappedBy = "portfolio",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Contact> contacts;

    @OneToMany(
        mappedBy = "portfolio",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Project> projects;

    @OneToMany(
        mappedBy = "portfolio",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Section> sections;

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
        List<Section> sections
    ) {
        this.id = id;
        this.profileImageShown = profileImageShown;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.contacts = contacts;
        this.projects = projects;
        this.sections = sections;
    }

    public void updateProfileImageShown(boolean profileImageShown) {
        this.profileImageShown = profileImageShown;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateContacts(List<Contact> sources) {
        updateExistingContacts(sources);
        addNonExistingContacts(sources);
        removeUselessContacts(sources);
    }

    private void updateExistingContacts(List<Contact> sources) {
        for (Contact source : sources) {
            updateExistingContact(source, getContactsWithId());
        }
    }

    private void updateExistingContact(Contact source, Map<Long, Contact> contactsWithId) {
        if (contactsWithId.containsKey(source.getId())) {
            Contact target = contactsWithId.get(source.getId());

            target.updateCategory(source.getCategory());
            target.updateValue(source.getValue());
        }
    }

    private Map<Long, Contact> getContactsWithId() {
        return contacts.stream()
            .collect(toMap(Contact::getId, Function.identity()));
    }

    private void addNonExistingContacts(List<Contact> sources) {
        List<Contact> nonExistingContacts = sources.stream()
            .filter(source -> !contacts.contains(source))
            .collect(toList());
        contacts.addAll(nonExistingContacts);
    }

    private void removeUselessContacts(List<Contact> sources) {
        contacts.removeIf(contact -> !sources.contains(contact));
    }

    public void updateProjects(List<Project> sources) {
        updateExistingProjects(sources);
        addNonExistingProjects(sources);
        removeUselessProjects(sources);
    }

    private void updateExistingProjects(List<Project> sources) {
        for (Project source : sources) {
            updateExistingProject(source, getProjectsWithId());
        }
    }

    private void updateExistingProject(Project source, Map<Long, Project> projectsWithId) {
        if (projectsWithId.containsKey(source.getId())) {
            Project project = projectsWithId.get(source.getId());

            project.updateName(source.getName());
            project.updateStartDate(source.getStartDate());
            project.updateEndDate(source.getEndDate());
            project.updateType(source.getType());
            project.updateImageUrl(source.getImageUrl());
            project.updateContent(source.getContent());
            project.updateTags(source.getTags());
        }
    }

    private Map<Long, Project> getProjectsWithId() {
        return projects.stream()
            .collect(toMap(Project::getId, Function.identity()));
    }

    private void addNonExistingProjects(List<Project> sources) {
        List<Project> nonExistingProjects = sources.stream()
            .filter(source -> !projects.contains(source))
            .collect(toList());
        projects.addAll(nonExistingProjects);
    }

    private void removeUselessProjects(List<Project> sources) {
        projects.removeIf(project -> !sources.contains(project));
    }

    public void updateSections(List<Section> sources) {
        updateExistingSections(sources);
        addNonExistingSections(sources);
        removeUselessSections(sources);
    }

    private void updateExistingSections(List<Section> sources) {
        for (Section source : sources) {
            updateExistingSection(source, getSectionsWithId());
        }
    }

    private void updateExistingSection(Section source, Map<Long, Section> sectionsWithId) {
        if (sectionsWithId.containsKey(source.getId())) {
            Section section = sectionsWithId.get(source.getId());

            section.updateName(section.getName());
            section.updateItems(section.getItems());
        }
    }

    private Map<Long, Section> getSectionsWithId() {
        return sections.stream()
            .collect(toMap(Section::getId, Function.identity()));
    }

    private void addNonExistingSections(List<Section> sources) {
        List<Section> nonExistingSections = sources.stream()
            .filter(source -> !sections.contains(source))
            .collect(toList());
        sections.addAll(nonExistingSections);
    }

    private void removeUselessSections(List<Section> sources) {
        sections.removeIf(section -> !sources.contains(section));
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

    public List<Contact> getContacts() {
        return contacts;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Section> getSections() {
        return sections;
    }
}
