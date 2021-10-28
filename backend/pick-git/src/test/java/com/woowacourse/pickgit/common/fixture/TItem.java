package com.woowacourse.pickgit.common.fixture;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.ItemRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.DescriptionResponse;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ItemResponse;
import java.util.List;
import java.util.UUID;

public class TItem {

    private final ItemResponse itemResponse;

    public TItem(ItemResponse itemResponse) {
        this.itemResponse = itemResponse;
    }

    public static ItemRequest create(String category) {
        return new ItemRequest(
            null,
            category,
            List.of(TDescription.createRandom())
        );
    }

    public static ItemRequest createRandom() {
        return new ItemRequest(
            null,
            UUID.randomUUID().toString(),
            List.of(TDescription.createRandom())
        );
    }

    public Modifier modifier() {
        return new Modifier(itemResponse);
    }

    public static class Modifier {

        private final ItemResponse itemResponse;

        public Modifier(ItemResponse itemResponse) {
            this.itemResponse = itemResponse;
        }

        private String category;
        private List<DescriptionRequest> descriptions;

        public Modifier category(String category) {
            this.category = category;
            return this;
        }

        public Modifier descriptions(List<DescriptionRequest> descriptions) {
            this.descriptions = descriptions;
            return this;
        }

        public ItemRequest build() {
            return ItemRequest.builder()
                .id(itemResponse.getId())
                .category(category == null ? itemResponse.getCategory() : category)
                .descriptions(descriptions == null ?
                    toDescriptionRequests(itemResponse.getDescriptions()) : descriptions)
                .build();
        }

        public List<DescriptionRequest> toDescriptionRequests(
            List<DescriptionResponse> descriptionResponses
        ) {
            return descriptionResponses.stream()
                .map(description -> new TDescription(description).modifier().build())
                .collect(toList());
        }
    }

}
