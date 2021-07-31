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

    public GithubProfile(
        String githubUrl,
        String company,
        String location,
        String website,
        String twitter
    ) {
        this.githubUrl = githubUrl;
        this.company = company;
        this.location = location;
        this.website = website;
        this.twitter = twitter;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getWebsite() {
        return website;
    }

    public String getTwitter() {
        return twitter;
    }
}
