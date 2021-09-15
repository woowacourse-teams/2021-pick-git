package com.woowacourse.pickgit.portfolio.domain;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    private List<Section> sections;

    protected Sections() {
        this(new ArrayList<>());
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> updateSections(List<Section> sources) {
        updateExistingSections(sources);
        addNonExistingSections(sources);
        removeUselessSections(sources);

        return sections;
    }

    private void updateExistingSections(List<Section> sources) {
        for (Section source : sources) {
            updateExistingSection(source, getSectionsWithId());
        }
    }

    private void updateExistingSection(Section source, Map<Long, Section> sectionsWithId) {
        if (sectionsWithId.containsKey(source.getId())) {
            Section section = sectionsWithId.get(source.getId());

            section.updateName(section.getName());
            section.updateItems(section.getItems());
        }
    }

    private Map<Long, Section> getSectionsWithId() {
        return sections.stream()
            .collect(toMap(Section::getId, Function.identity()));
    }

    private void addNonExistingSections(List<Section> sources) {
        List<Section> nonExistingSections = sources.stream()
            .filter(source -> !sections.contains(source))
            .collect(toList());
        sections.addAll(nonExistingSections);
    }

    private void removeUselessSections(List<Section> sources) {
        sections.removeIf(section -> !sources.contains(section));
    }

    public void add(Section section) {
        sections.add(section);
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    public List<Section> getSections() {
        return sections;
    }
}
