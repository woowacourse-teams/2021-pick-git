package com.woowacourse.pickgit.authentication.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import lombok.Builder;

@Builder
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

    private OAuthProfileResponse() {
    }

    public OAuthProfileResponse(
        String name,
        String image,
        String description,
        String githubUrl,
        String company,
        String location,
        String website,
        String twitter
    ) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.githubUrl = githubUrl;
        this.company = company;
        this.location = location;
        this.website = website;
        this.twitter = twitter;
    }

    public GithubProfile toGithubProfile() {
        return new GithubProfile(githubUrl, company, location, website, twitter);
    }

    public BasicProfile toBasicProfile() {
        return new BasicProfile(name, image, description);
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
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
