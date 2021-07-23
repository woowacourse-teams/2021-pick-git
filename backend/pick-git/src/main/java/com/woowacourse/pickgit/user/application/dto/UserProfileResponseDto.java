package com.woowacourse.pickgit.user.application.dto;

import lombok.Builder;

@Builder
public class UserProfileResponseDto {

    private final String name;
    private final String image;
    private final String description;

    private final int followerCount;
    private final int followingCount;
    private final int postCount;

    private final String githubUrl;
    private final String company;
    private final String location;
    private final String website;
    private final String twitter;

    private final Boolean following;

    public UserProfileResponseDto(String name, String image, String description,
        int followerCount, int followingCount, int postCount, String githubUrl, String company,
        String location, String website, String twitter, Boolean following) {
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
