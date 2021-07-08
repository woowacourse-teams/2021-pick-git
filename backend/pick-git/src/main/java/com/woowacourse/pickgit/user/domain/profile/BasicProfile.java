package com.woowacourse.pickgit.user.domain.profile;

import javax.persistence.Embeddable;

@Embeddable
public class BasicProfile {

    private String image;

    private String description;

    protected BasicProfile() {
    }

    public BasicProfile(String image, String description) {
        this.image = image;
        this.description = description;
    }
}
