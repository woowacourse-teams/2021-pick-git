package com.woowacourse.pickgit.portfolio.domain;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    public void updateCategory(String category) {
        this.category = category;
    }

    public void updateDescriptions(List<Description> sources) {
        updateExistingDescriptions(sources);
        addNonExistingDescriptions(sources);
        removeUselessDescriptions(sources);
    }

    private void updateExistingDescriptions(List<Description> sources) {
        for (Description source : sources) {
            updateExistingDescription(source, getDescriptionsWithId());
        }
    }

    private void updateExistingDescription(
        Description source,
        Map<Long, Description> descriptionsWithId
    ) {
        if (descriptionsWithId.containsKey(source.getId())) {
            Description description = descriptionsWithId.get(source.getId());

            description.updateValue(source.getValue());
        }
    }

    private Map<Long, Description> getDescriptionsWithId() {
        return descriptions.stream()
            .collect(toMap(Description::getId, Function.identity()));
    }

    private void addNonExistingDescriptions(List<Description> sources) {
        List<Description> nonExistingDescriptions = sources.stream()
            .filter(source -> !descriptions.contains(source))
            .collect(toList());
        descriptions.addAll(nonExistingDescriptions);
    }

    private void removeUselessDescriptions(List<Description> sources) {
        descriptions.removeIf(description -> !sources.contains(description));
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
