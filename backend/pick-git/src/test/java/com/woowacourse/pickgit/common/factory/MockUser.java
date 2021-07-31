package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.post.domain.Posts;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import com.woowacourse.pickgit.user.domain.follow.Followings;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;

public class MockUser {

    private MockUser() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Followers followers = new Followers();
        private final Followings followings = new Followings();
        private final Posts posts = new Posts();

        private Long id;
        private String name;
        private String image = "http://img.com";
        private String description = "The Best";
        private String githubUrl = "https://github.com/yjksw";
        private String company = "woowacourse";
        private String location = "Seoul";
        private String website = "www.pick-git.com";
        private String twitter = "pick-git twitter";

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public User build() {
            return new User(
                id,
                new BasicProfile(name, image, description),
                new GithubProfile(githubUrl, company, location, website, twitter),
                new Followers(),
                new Followings(),
                new Posts()
            );
        }
    }
}
