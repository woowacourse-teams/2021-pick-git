package com.woowacourse.pickgit.user.domain.profile;

import com.woowacourse.pickgit.user.domain.Vendor;
import javax.persistence.Embeddable;

@Embeddable
public class GithubProfile {

    private String githubUrl;
    private String company;
    private String location;
    private String website;
    private String twitter;

    public GithubProfile() {
    }

    public GithubProfile(String githubUrl, String company, String location, String website,
        String twitter) {
        this.githubUrl = githubUrl;
        this.company = company;
        this.location = location;
        this.website = website;
        this.twitter = twitter;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
}
