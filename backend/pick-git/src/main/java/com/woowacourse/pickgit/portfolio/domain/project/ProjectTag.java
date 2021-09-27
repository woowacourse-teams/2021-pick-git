package com.woowacourse.pickgit.portfolio.domain.project;

import com.woowacourse.pickgit.portfolio.domain.common.Updatable;
import com.woowacourse.pickgit.tag.domain.Tag;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ProjectTag implements Updatable<ProjectTag> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    protected ProjectTag() {
    }

    public ProjectTag(Long id, Tag tag) {
        this(id, tag, null);
    }

    public ProjectTag(Long id, Tag tag, Project project) {
        this.id = id;
        this.tag = tag;
        this.project = project;
    }

    public void appendTo(Project project) {
        this.project = project;
    }

    public String getName() {
        return tag.getName();
    }

    public Long getId() {
        return id;
    }

    public Tag getTag() {
        return tag;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void update(ProjectTag projectTag) {
        this.tag = projectTag.getTag();
    }
}
