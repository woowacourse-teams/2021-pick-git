package com.woowacourse.pickgit.portfolio.domain.project;

import com.woowacourse.pickgit.portfolio.domain.common.Updatable;
import com.woowacourse.pickgit.tag.domain.Tag;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tag_id", "project_id"})
    }
)
public class ProjectTag implements Updatable<ProjectTag> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    protected ProjectTag() {
    }

    public ProjectTag(Tag tag) {
        this(null, tag, null);
    }

    public ProjectTag(Long id, Tag tag, Project project) {
        this.id = id;
        this.tag = tag;
        this.project = project;
    }

    public void appendTo(Project project) {
        this.project = project;
    }

    public Long getTagId() {
        return tag.getId();
    }

    public String getTagName() {
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

    @Override
    public boolean semanticallyEquals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectTag)) {
            return false;
        }

        return this.getTag().equals(((ProjectTag) o).getTag());
    }

    @Override
    public int semanticallyHashcode() {
        return this.getTag().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectTag)) {
            return false;
        }
        ProjectTag that = (ProjectTag) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
