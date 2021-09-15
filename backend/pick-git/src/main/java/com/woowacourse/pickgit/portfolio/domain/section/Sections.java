package com.woowacourse.pickgit.portfolio.domain.section;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.common.UpdateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(
        mappedBy = "portfolio",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Section> values;

    protected Sections() {
        this(new ArrayList<>());
    }

    public Sections(List<Section> values) {
        this.values = values;
    }

    public void appendTo(Portfolio portfolio) {
        this.values.forEach(section -> section.appendTo(portfolio));
    }

    public void update(Sections sources, Portfolio portfolio) {
        sources.values.forEach(source -> source.appendTo(portfolio));

        UpdateUtil.execute(this.values, sources.values);
    }

    public void add(Section section) {
        values.add(section);
    }

    public void remove(Section section) {
        values.remove(section);
    }

    public List<Section> getValues() {
        return values;
    }
}
