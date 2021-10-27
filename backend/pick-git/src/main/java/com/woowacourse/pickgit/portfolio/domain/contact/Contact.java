package com.woowacourse.pickgit.portfolio.domain.contact;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.common.Updatable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Contact implements Updatable<Contact> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    protected Contact() {
    }

    public Contact(Long id, String category, String value) {
        this(id, category, value, null);
    }

    public Contact(
        Long id,
        String category,
        String value,
        Portfolio portfolio
    ) {
        this.id = id;
        this.category = category;
        this.value = value;
        this.portfolio = portfolio;
    }

    public void appendTo(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        return value;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    @Override
    public void update(Contact contact) {
        this.category = contact.getCategory();
        this.value = contact.getValue();
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
        if (!(o instanceof Contact)) {
            return false;
        }
        Contact contact = (Contact) o;
        return Objects.equals(getId(), contact.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
