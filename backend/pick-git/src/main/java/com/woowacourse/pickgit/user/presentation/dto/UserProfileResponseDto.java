package com.woowacourse.pickgit.user.presentation.dto;

import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;

public class UserProfileResponseDto {

    private final String name;
    private final String image;
    private final String description;

    private final int followerCount;
    private final int followingCount;
    private final int postCount;

    private final String company;
    private final String location;
    private final String webSite;
    private final String twitter;

    public UserProfileResponseDto(UserProfileServiceDto userProfileServiceDto) {
        this.name = userProfileServiceDto.getName();
        this.image = userProfileServiceDto.getImage();
        this.description = userProfileServiceDto.getDescription();
        this.followerCount = userProfileServiceDto.getFollowerCount();
        this.followingCount = userProfileServiceDto.getFollowingCount();
        this.postCount = userProfileServiceDto.getPostCount();
        this.company = userProfileServiceDto.getCompany();
        this.location = userProfileServiceDto.getLocation();
        this.webSite = userProfileServiceDto.getWebSite();
        this.twitter = userProfileServiceDto.getTwitter();
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
