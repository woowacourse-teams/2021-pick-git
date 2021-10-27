package com.woowacourse.pickgit.portfolio.domain.section;

import static java.util.stream.Collectors.toSet;

import com.woowacourse.pickgit.exception.portfolio.DuplicateSectionNameException;
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
public class Sections {

    @OneToMany(
        mappedBy = "portfolio",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private final List<Section> values;

    protected Sections() {
        this(new ArrayList<>());
    }

    public Sections(List<Section> values) {
        this.values = values;

        PortfolioValidator.sectionSize(values);
    }

    public static Sections empty() {
        return new Sections(new ArrayList<>());
    }

    public void appendTo(Portfolio portfolio) {
        this.getValues().forEach(section -> section.appendTo(portfolio));
    }

    public void add(Section section) {
        values.add(section);
    }

    public void remove(Section section) {
        values.remove(section);
    }

    public void update(Sections sources, Portfolio portfolio) {
        List<Section> sourceValues = sources.getValues();

        if (isDuplicateName(sourceValues)) {
            throw new DuplicateSectionNameException();
        }

        sourceValues.forEach(source -> source.appendTo(portfolio));

        UpdateUtil.execute(this.getValues(), sourceValues);
    }

    private boolean isDuplicateName(List<Section> sourceValues) {
        return sourceValues.size() != sourceValues.stream()
            .map(Section::getName)
            .collect(toSet())
            .size();
    }

    public List<Section> getValues() {
        return values;
    }
}
