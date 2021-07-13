package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.post.domain.Posts;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import com.woowacourse.pickgit.user.domain.follow.Followings;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private BasicProfile basicProfile;

    @Embedded
    private GithubProfile githubProfile;

    @Embedded
    private Followers followers = new Followers();

    @Embedded
    private Followings followings = new Followings();

    @Embedded
    private Posts posts;

    protected User() {
    }

    public User(BasicProfile basicProfile,
        GithubProfile githubProfile) {
        this.basicProfile = basicProfile;
        this.githubProfile = githubProfile;
    }

    public void changeBasicProfile(BasicProfile basicProfile) {
        this.basicProfile = basicProfile;
    }

    public void changeGithubProfile(GithubProfile githubProfile) {
        this.githubProfile = githubProfile;
    }

    public void follow(User target) {
    }

    public void unfollow(User target) {
    }

    public BasicProfile getBasicProfile() {
        return basicProfile;
    }

    public GithubProfile getGithubProfile() {
        return githubProfile;
    }
}
