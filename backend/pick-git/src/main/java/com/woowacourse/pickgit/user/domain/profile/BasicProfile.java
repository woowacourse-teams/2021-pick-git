package com.woowacourse.pickgit.user.domain.profile;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BasicProfile {

    @Column(nullable = false, updatable = false)
    private String name;

    private String image;

    private String description;

    protected BasicProfile() {
    }
}
