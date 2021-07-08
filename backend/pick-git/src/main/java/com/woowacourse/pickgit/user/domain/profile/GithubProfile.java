package com.woowacourse.pickgit.user.domain.profile;

import com.woowacourse.pickgit.user.domain.Vendor;

public class GithubProfile {

    private final String company;
    private final String location;
    private final String webSite;
    private final String twitter;
    private String name;

    public GithubProfile(String company, String location, String webSite, String twitter,
        Vendor vendor) {
        this.company = company;
        this.location = location;
        this.webSite = webSite;
        this.twitter = twitter;
    }

}
