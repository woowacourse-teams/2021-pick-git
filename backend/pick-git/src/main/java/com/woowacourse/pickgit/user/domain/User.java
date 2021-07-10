package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.post.domain.Posts;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import com.woowacourse.pickgit.user.domain.follow.Followings;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import com.woowacourse.pickgit.user.domain.statistics.UserStatistics;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Embedded
    private BasicProfile basicProfile;

    @Embedded
    private Followers followers;

    @Embedded
    private Followings followings;

    @Embedded
    private Posts posts;

    @Embedded
    private GithubProfile githubProfile;

    @Transient
    private UserStatistics statistics;

    public User() {
    }

    public User(BasicProfile basicProfile,
        GithubProfile githubProfile) {
        this.basicProfile = basicProfile;
        this.githubProfile = githubProfile;
    }

    public User(Long id, BasicProfile basicProfile,
        Followers followers, Followings followings, Posts posts,
        GithubProfile githubProfile,
        UserStatistics statistics) {
        this.id = id;
        this.basicProfile = basicProfile;
        this.followers = followers;
        this.followings = followings;
        this.posts = posts;
        this.githubProfile = githubProfile;
        this.statistics = statistics;
    }

    public Long getId() {
        return id;
    }

    public BasicProfile getBasicProfile() {
        return basicProfile;
    }

    public Followers getFollowers() {
        return followers;
    }

    public Followings getFollowings() {
        return followings;
    }

    public Posts getPosts() {
        return posts;
    }

    public GithubProfile getGithubProfile() {
        return githubProfile;
    }

    public UserStatistics getStatistics() {
        return statistics;
    }

    public void setGithubProfile(GithubProfile githubProfile) {
        this.githubProfile = githubProfile;
    }
}
