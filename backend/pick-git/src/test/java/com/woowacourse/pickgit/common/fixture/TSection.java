package com.woowacourse.pickgit.common.fixture;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.SectionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ItemResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.SectionResponse;
import java.util.List;
import java.util.UUID;

public class TSection {

    private final SectionResponse sectionResponse;

    public TSection(SectionResponse sectionResponse) {
        this.sectionResponse = sectionResponse;
    }

    public static SectionRequest createRandom() {
        return new SectionRequest(
            null,
            UUID.randomUUID().toString(),
            List.of(TItem.createRandom())
        );
    }

    public static SectionRequest of() {
        return new SectionRequest(
            null,
            "test name",
            List.of(TItem.createRandom())
        );
    }

    public Modifier modifier() {
        return new Modifier(sectionResponse);
    }

    public static class Modifier {

        private final SectionResponse sectionResponse;

        private String name;
        private List<ItemRequest> items;

        public Modifier(SectionResponse sectionResponse) {
            this.sectionResponse = sectionResponse;
        }

        public Modifier name(String name) {
            this.name = name;
            return this;
        }

        public Modifier items(ItemRequest... items) {
            this.items = List.of(items);
            return this;
        }

        public SectionRequest build() {
            return SectionRequest.builder()
                .id(sectionResponse.getId())
                .name(name == null ? sectionResponse.getName() : name)
                .items(items == null ? toItemRequests(sectionResponse.getItems()) : items)
                .build();
        }

        public List<ItemRequest> toItemRequests(List<ItemResponse> itemResponses) {
            return itemResponses.stream()
                .map(item -> new TItem(item).modifier().build())
                .collect(toList());
        }
    }
}
