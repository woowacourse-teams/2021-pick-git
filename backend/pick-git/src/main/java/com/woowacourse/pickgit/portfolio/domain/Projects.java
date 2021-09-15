package com.woowacourse.pickgit.portfolio.domain;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Projects {

    @OneToMany(
        mappedBy = "portfolio",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Project> projects;

    protected Projects() {
        this(new ArrayList<>());
    }

    public Projects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Project> updateProjects(List<Project> sources) {
        updateExistingProjects(sources);
        addNonExistingProjects(sources);
        removeUselessProjects(sources);

        return projects;
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

    public void add(Project project) {
        projects.add(project);
    }

    public void remove(Project project) {
        projects.remove(project);
    }

    public List<Project> getProjects() {
        return projects;
    }
}
