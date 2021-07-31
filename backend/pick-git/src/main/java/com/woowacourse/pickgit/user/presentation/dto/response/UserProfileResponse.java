package com.woowacourse.pickgit.user.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserProfileResponse {

    private String name;
    private String imageUrl;
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
        String name,
        String imageUrl,
        String description,
        int followerCount,
        int followingCount,
        int postCount,
        String githubUrl,
        String company,
        String location,
        String website,
        String twitter,
        Boolean following
    ) {
        this.name = name;
        this.imageUrl = imageUrl;
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
}
