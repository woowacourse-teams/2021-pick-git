package com.woowacourse.pickgit.common.fixture;

import com.woowacourse.pickgit.portfolio.presentation.dto.request.ContactRequest;
import com.woowacourse.pickgit.portfolio.presentation.dto.response.ContactResponse;
import java.util.UUID;

public class TContact {

    private final ContactResponse contactResponse;

    public TContact(ContactResponse contactResponse) {
        this.contactResponse = contactResponse;
    }

    public static ContactRequest createRandom() {
        return new ContactRequest(
            null,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        );
    }

    public Modifier modifier() {
        return new Modifier(contactResponse);
    }

    public static class Modifier {

        private final ContactResponse contactResponse;
        private String category;
        private String value;

        public Modifier(ContactResponse contactResponse) {
            this.contactResponse = contactResponse;
        }

        public Modifier category(String category) {
            this.category = category;
            return this;
        }

        public Modifier value(String value) {
            this.value = value;
            return this;
        }

        public ContactRequest build() {
            return ContactRequest.builder()
                .id(contactResponse.getId())
                .category(category == null ? contactResponse.getCategory() : category)
                .value(value == null ? contactResponse.getValue() : value)
                .build();
        }
    }
}
