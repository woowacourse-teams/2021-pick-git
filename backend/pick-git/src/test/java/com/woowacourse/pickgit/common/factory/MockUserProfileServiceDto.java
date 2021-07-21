package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;

public class MockUserProfileServiceDto {

    private MockUserProfileServiceDto() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder followerCount(int followerCount) {
            this.followerCount = followerCount;
            return this;
        }

        public Builder followingCount(int followingCount) {
            this.followingCount = followingCount;
            return this;
        }

        public Builder postCount(int postCount) {
            this.postCount = postCount;
            return this;
        }

        public Builder githubUrl(String githubUrl) {
            this.githubUrl = githubUrl;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder website(String website) {
            this.website = website;
            return this;
        }

        public Builder twitter(String twitter) {
            this.twitter = twitter;
            return this;
        }

        public Builder following(Boolean following) {
            this.following = following;
            return this;
        }

        public UserProfileServiceDto build() {
            return new UserProfileServiceDto(
                name, image, description, followerCount,
                followingCount, postCount, githubUrl,
                company, location, website, twitter, following
            );
        }
    }

}
