package com.woowacourse.pickgit.user.presentation.dto.response;

import lombok.Builder;

@Builder
public class UserProfileResponse {

    private String name;
    private String image;
    private String description;
    private int followerCount;
    private int followingCount;
    private int postCount;
    private String githubUrl;
    private String company;
    private String location;
    private String website;
    private String twitter;
    private Boolean following;

    private UserProfileResponse() {
    }

    public UserProfileResponse(
        String name, String image, String description,
        int followerCount, int followingCount, int postCount,
        String githubUrl, String company, String location, String website, String twitter,
        Boolean following) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.postCount = postCount;
        this.githubUrl = githubUrl;
        this.company = company;
        this.location = location;
        this.website = website;
        this.twitter = twitter;
        this.following = following;
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

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getPostCount() {
        return postCount;
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

    public Boolean getFollowing() {
        return following;
    }
}
