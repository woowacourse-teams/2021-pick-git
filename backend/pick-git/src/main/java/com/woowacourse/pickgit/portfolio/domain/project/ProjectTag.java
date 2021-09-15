package com.woowacourse.pickgit.portfolio.domain.project;

import com.woowacourse.pickgit.tag.domain.Tag;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ProjectTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    protected ProjectTag() {
    }

    public ProjectTag(Long id, Project project, Tag tag) {
        this.id = id;
        this.project = project;
        this.tag = tag;
    }

    public void addProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return tag.getName();
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public Tag getTag() {
        return tag;
    }
}
