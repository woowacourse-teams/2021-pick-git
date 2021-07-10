package com.woowacourse.pickgit.authentication.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthProfileResponse {

    @JsonProperty("login")
    private String username;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
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
