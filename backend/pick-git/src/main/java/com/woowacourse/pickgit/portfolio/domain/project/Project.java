package com.woowacourse.pickgit.portfolio.domain.project;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.PortfolioValidator;
import com.woowacourse.pickgit.portfolio.domain.common.Updatable;
import com.woowacourse.pickgit.portfolio.domain.common.UpdateUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Project implements Updatable<Project> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false)
    @Enumerated
    private ProjectType type;

    @Column(nullable = false)
    private String imageUrl;

    @Lob
    private String content;

    @OneToMany(
        mappedBy = "project",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<ProjectTag> tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    protected Project() {
    }

    public Project(
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String type,
        String imageUrl,
        String content,
        List<ProjectTag> tags
    ) {
        this(
            id,
            name,
            startDate,
            endDate,
            ProjectType.of(type),
            imageUrl,
            content,
            tags,
            null
        );
    }

    public Project(
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        ProjectType type,
        String imageUrl,
        String content,
        List<ProjectTag> tags,
        Portfolio portfolio
    ) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.imageUrl = imageUrl;
        this.content = content;
        this.tags = tags;
        this.portfolio = portfolio;

        PortfolioValidator.projectContent(content);

        this.tags.forEach(tag -> tag.appendTo(this));
    }

    public void appendTo(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public ProjectType getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContent() {
        return content;
    }

    public List<ProjectTag> getTags() {
        return tags;
    }

    @Override
    public void update(Project project) {
        project.getTags().forEach(tag -> tag.appendTo(this));

        this.name = project.getName();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.type = project.getType();
        this.imageUrl = project.getImageUrl();
        this.content = project.getContent();
        UpdateUtil.execute(this.getTags(), project.getTags());
    }

    @Override
    public boolean semanticallyEquals(Object o) {
        return equals(o);
    }

    @Override
    public int semanticallyHashcode() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        Project project = (Project) o;
        return Objects.equals(id, project.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
