package com.woowacourse.pickgit.portfolio.domain.project;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
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
        this.getValues().forEach(project -> project.appendTo(portfolio));

        UpdateUtil.execute(this.getValues(), sources.getValues());
    }

    public List<Project> getValues() {
        return values;
    }
}
