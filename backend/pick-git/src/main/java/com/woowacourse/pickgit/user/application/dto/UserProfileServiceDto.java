package com.woowacourse.pickgit.user.application.dto;

public class UserProfileServiceDto {

    private final String name;
    private final String image;
    private final String description;

    private final int followerCount;
    private final int followingCount;
    private final int postCount;

    private final String githubUrl;
    private final String company;
    private final String location;
    private final String webSite;
    private final String twitter;

    public UserProfileServiceDto(String name, String image, String description,
        int followerCount, int followingCount, int postCount, String githubUrl, String company,
        String location, String webSite, String twitter) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.postCount = postCount;
        this.githubUrl = githubUrl;
        this.company = company;
        this.location = location;
        this.webSite = webSite;
        this.twitter = twitter;
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

    public String getWebSite() {
        return webSite;
    }

    public String getTwitter() {
        return twitter;
    }
}
