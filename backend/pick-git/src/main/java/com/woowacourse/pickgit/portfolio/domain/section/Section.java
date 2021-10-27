package com.woowacourse.pickgit.portfolio.domain.section;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.PortfolioValidator;
import com.woowacourse.pickgit.portfolio.domain.common.Updatable;
import com.woowacourse.pickgit.portfolio.domain.common.UpdateUtil;
import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Section implements Updatable<Section> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(
        mappedBy = "section",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Item> items;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    protected Section() {
    }

    public Section(Long id, String name, List<Item> items) {
        this(id, name, items, null);
    }

    public Section(
        Long id,
        String name,
        List<Item> items,
        Portfolio portfolio
    ) {
        this.id = id;
        this.name = name;
        this.items = items;
        this.portfolio = portfolio;

        PortfolioValidator.sectionCategorySize(items);

        this.items.forEach(item -> item.appendTo(this));
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

    public List<Item> getItems() {
        return items;
    }

    @Override
    public void update(Section section) {
        section.getItems().forEach(item -> item.appendTo(this));

        this.name = section.name;
        UpdateUtil.execute(this.items, section.getItems());
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
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
