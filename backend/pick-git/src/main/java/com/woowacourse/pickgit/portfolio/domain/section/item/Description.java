package com.woowacourse.pickgit.portfolio.domain.section.item;

import com.woowacourse.pickgit.portfolio.domain.PortfolioValidator;
import com.woowacourse.pickgit.portfolio.domain.common.Updatable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Description implements Updatable<Description> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    protected Description() {
    }

    public Description(Long id, String value) {
        this(id, value, null);
    }

    public Description(Long id, String value, Item item) {
        this.id = id;
        this.value = value;
        this.item = item;

        PortfolioValidator.sectionDescription(value);
    }

    public void appendTo(Item item) {
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void update(Description description) {
        this.value = description.getValue();
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
        if (!(o instanceof Description)) {
            return false;
        }
        Description that = (Description) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
