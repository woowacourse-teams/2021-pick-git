package com.woowacourse.pickgit.authentication.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;

public class OAuthProfileResponse {

    @JsonProperty("login")
    private String name;

    @JsonProperty("avatar_url")
    private String image;

    @JsonProperty("bio")
    private String description;

    @JsonProperty("html_url")
    private String githubUrl;

    private String company;

    private String location;

    @JsonProperty("blog")
    private String website;

    @JsonProperty("twitter_username")
    private String twitter;

    public OAuthProfileResponse() {
    }

    public OAuthProfileResponse(String name, String image, String description,
        String githubUrl, String company, String location, String website, String twitter) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.githubUrl = githubUrl;
        this.company = company;
        this.location = location;
        this.website = website;
        this.twitter = twitter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public GithubProfile toGithubProfile() {
        return new GithubProfile(
            githubUrl,
            company,
            location,
            website,
            twitter
        );
    }

    public BasicProfile toBasicProfile() {
        return new BasicProfile(
            name,
            image,
            description
        );
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
