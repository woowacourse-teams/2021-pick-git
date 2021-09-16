package com.woowacourse.pickgit.portfolio.domain.section.item;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.woowacourse.pickgit.portfolio.domain.common.Updatable;
import com.woowacourse.pickgit.portfolio.domain.common.UpdateUtil;
import com.woowacourse.pickgit.portfolio.domain.section.Section;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Item implements Updatable<Item> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @OneToMany(
        mappedBy = "item",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Description> descriptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    protected Item() {
    }

    public Item(Long id, String category, List<Description> descriptions) {
        this(id, category, descriptions, null);
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

        this.descriptions.forEach(description -> description.appendTo(this));
    }

    public void appendTo(Section section) {
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

    @Override
    public void update(Item item) {
        getDescriptions(item).forEach(description -> description.appendTo(this));

        UpdateUtil.execute(item.descriptions, this.descriptions);
    }

    private List<Description> getDescriptions(Item item) {
        return item.descriptions;
    }
}
