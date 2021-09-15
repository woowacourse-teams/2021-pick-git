package com.woowacourse.pickgit.portfolio.domain.section;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.woowacourse.pickgit.portfolio.domain.Portfolio;
import com.woowacourse.pickgit.portfolio.domain.section.item.Item;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
        List<Item> items,
        Portfolio portfolio
    ) {
        this.id = id;
        this.name = name;
        this.portfolio = portfolio;
        this.items = items;

        for (Item item : items) {
            item.addSection(this);
        }
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateItems(List<Item> sources) {
        updateExistingItems(sources);
        addNonExistingItems(sources);
        removeUselessItems(sources);
    }

    private void updateExistingItems(List<Item> sources) {
        for (Item source : sources) {
            updateExistingItem(source, getItemsWithId());
        }
    }

    private void updateExistingItem(Item source, Map<Long, Item> itemsWithId) {
        if (itemsWithId.containsKey(source.getId())) {
            Item item = itemsWithId.get(source.getId());

            item.updateCategory(source.getCategory());
            item.updateDescriptions(source.getDescriptions());
        }
    }

    private Map<Long, Item> getItemsWithId() {
        return items.stream()
            .collect(toMap(Item::getId, Function.identity()));
    }

    private void addNonExistingItems(List<Item> sources) {
        List<Item> nonExistingItems = sources.stream()
            .filter(source -> !items.contains(source))
            .collect(toList());
        items.addAll(nonExistingItems);
    }

    private void removeUselessItems(List<Item> sources) {
        items.removeIf(item -> !sources.contains(item));
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

    public void linkPortfolio(Portfolio portfolio) {
        if (portfolio != null) {
            portfolio.removeSection(this);
        }
        this.portfolio = portfolio;
    }

    public void unlinkPortfolio(Portfolio portfolio) {
        this.portfolio = null;
    }
}
