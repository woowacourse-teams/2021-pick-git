package com.woowacourse.pickgit.portfolio.domain.project;

import static java.util.stream.Collectors.toSet;

import com.woowacourse.pickgit.exception.portfolio.DuplicateProjectException;
import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.PortfolioValidator;
import com.woowacourse.pickgit.portfolio.domain.common.UpdateUtil;
import java.util.ArrayList;
import java.util.List;
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
    private final List<Project> values;

    protected Projects() {
        this(new ArrayList<>());
    }

    public Projects(List<Project> values) {
        this.values = values;

        PortfolioValidator.projectSize(values);
    }

    public static Projects empty() {
        return new Projects(new ArrayList<>());
    }

    public void appendTo(Portfolio portfolio) {
        this.getValues().forEach(project -> project.appendTo(portfolio));
    }

    public void add(Project project) {
        values.add(project);
    }

    public void remove(Project project) {
        values.remove(project);
    }

    public void update(Projects sources, Portfolio portfolio) {
        List<Project> sourceValues = sources.getValues();

        if (isDuplicate(sourceValues)) {
            throw new DuplicateProjectException();
        }

        sourceValues.forEach(source -> source.appendTo(portfolio));

        UpdateUtil.execute(this.getValues(), sourceValues);
    }

    private boolean isDuplicate(List<Project> sourceValues) {
        return sourceValues.size() != sourceValues.stream()
            .map(Project::getName)
            .collect(toSet())
            .size();
    }

    public List<Project> getValues() {
        return values;
    }
}
