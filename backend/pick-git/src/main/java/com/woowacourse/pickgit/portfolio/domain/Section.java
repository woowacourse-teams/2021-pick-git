package com.woowacourse.pickgit.portfolio.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Section {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
    private List<Item> items;

    protected Section() {
    }

    public Section(
        Long id,
        String name,
        List<Item> items
    ) {
        this.id = id;
        this.name = name;
        this.items = items;
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
}
