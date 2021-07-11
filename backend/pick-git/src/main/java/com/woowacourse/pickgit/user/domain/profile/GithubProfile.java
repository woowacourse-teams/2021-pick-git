package com.woowacourse.pickgit.user.domain.profile;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GithubProfile {

    @Column(nullable = false, updatable = false)
    private String githubUrl;

    private String company;

    private String location;

    private String website;

    private String twitter;

    protected GithubProfile() {
    }
}
