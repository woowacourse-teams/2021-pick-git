package com.woowacourse.pickgit.portfolio.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Description> descriptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    protected Item() {
    }

    public Item(
        Long id,
        String category,
        List<Description> descriptions,
        Section section
    ) {
        this.id = id;
        this.category = category;
        this.descriptions = descriptions;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public List<Description> getDescriptions() {
        return descriptions;
    }

    public Section getSection() {
        return section;
    }
}
