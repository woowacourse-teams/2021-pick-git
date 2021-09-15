package com.woowacourse.pickgit.portfolio.domain.contact;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Contact {

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

    public void updateCategory(String category) {
        this.category = category;
    }

    public void updateValue(String value) {
        this.value = value;
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

    public void linkPortfolio(Portfolio portfolio) {
        if (portfolio != null) {
            portfolio.removeContact(this);
        }
        this.portfolio = portfolio;
    }

    public void unlinkPortfolio(Portfolio portfolio) {
        this.portfolio = null;
    }
}
