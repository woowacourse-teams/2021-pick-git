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
    private List<Project> value;

    protected Projects() {
        this(new ArrayList<>());
    }

    public Projects(List<Project> value) {
        this.value = value;
    }

    public void update(Projects sources, Portfolio portfolio) {
        sources.value.forEach(project -> project.appendTo(portfolio));

        UpdateUtil.execute(this.value, sources.value);
    }

    public void appendTo(Portfolio portfolio) {
        this.value.forEach(project -> project.appendTo(portfolio));
    }

    public void add(Project project) {
        value.add(project);
    }

    public void remove(Project project) {
        value.remove(project);
    }

    public List<Project> getValue() {
        return value;
    }
}
