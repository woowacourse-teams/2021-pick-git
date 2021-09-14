package com.woowacourse.pickgit.portfolio.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Project {

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

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectTag> tags;

    protected Project() {
    }

    public Project(
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        ProjectType type,
        String imageUrl,
        String content,
        List<ProjectTag> tags
    ) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.imageUrl = imageUrl;
        this.content = content;
        this.tags = tags;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void updateType(ProjectType type) {
        this.type = type;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateTags(List<ProjectTag> sources) {
        tags.removeIf(tag -> !sources.contains(tag));
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
}
