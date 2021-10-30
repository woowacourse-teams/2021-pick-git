package com.woowacourse.pickgit.common.fixture;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.DescriptionRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.DescriptionResponse;
import java.util.UUID;

public class TDescription {

    private final DescriptionResponse descriptionResponse;

    public TDescription(DescriptionResponse descriptionResponse) {
        this.descriptionResponse = descriptionResponse;
    }

    public static DescriptionRequest createRandom() {
        return new DescriptionRequest(
            null,
            UUID.randomUUID().toString()
        );
    }

    public Modifier modifier() {
        return new Modifier(descriptionResponse);
    }

    public static class Modifier {

        private final DescriptionResponse descriptionResponse;
        private String value;

        public Modifier(
            DescriptionResponse descriptionResponse) {
            this.descriptionResponse = descriptionResponse;
        }

        public Modifier value(String value) {
            this.value = value;
            return this;
        }

        public DescriptionRequest build() {
            return DescriptionRequest.builder()
                .id(descriptionResponse.getId())
                .value(value == null ? descriptionResponse.getValue() : value)
                .build();
        }
    }
}
