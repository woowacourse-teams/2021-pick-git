package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.Posts;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.user.domain.follow.Follow;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import com.woowacourse.pickgit.user.domain.follow.Followings;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.ArrayList;
import java.util.Objects;
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
    private Followers followers;

    @Embedded
    private Followings followings;

    @Embedded
    private Posts posts;

    protected User() {
    }

    public User(BasicProfile basicProfile, GithubProfile githubProfile) {
        this(null, basicProfile, githubProfile);
    }

    public User(Long id, BasicProfile basicProfile, GithubProfile githubProfile) {
        this(
            id,
            basicProfile,
            githubProfile,
            new Followers(new ArrayList<>()),
            new Followings(new ArrayList<>()),
            new Posts(new ArrayList<>())
        );
    }

    public User(
        Long id,
        BasicProfile basicProfile,
        GithubProfile githubProfile,
        Followers followers,
        Followings followings,
        Posts posts
    ) {
        this.id = id;
        this.basicProfile = basicProfile;
        this.githubProfile = githubProfile;
        this.followers = followers;
        this.followings = followings;
        this.posts = posts;
    }

    public void updateDescription(String description) {
        this.basicProfile.setDescription(description);
    }

    public void updateProfileImage(String imageUrl) {
        this.basicProfile.setImage(imageUrl);
    }

    public void follow(User target) {
        Follow follow = new Follow(this, target);
        this.followings.add(follow);
        target.followers.add(follow);
    }

    public void unfollow(User target) {
        Follow follow = new Follow(this, target);
        this.followings.remove(follow);
        target.followers.remove(follow);
    }

    public Boolean isFollowing(User targetUser) {
        return this.followings.isFollowing(targetUser);
    }

    //todo 너잘과 병합시 삭제
    public void addComment(Post post, Comment comment) {
        comment.writeBy(this);
        post.addComment(comment);
    }

    public void changeGithubProfile(GithubProfile githubProfile) {
        this.githubProfile = githubProfile;
    }

    public Long getId() {
        return id;
    }

    public int getFollowerCount() {
        return followers.count();
    }

    public int getFollowingCount() {
        return followings.count();
    }

    public int getPostCount() {
        return posts.count();
    }

    public String getName() {
        return basicProfile.getName();
    }

    public String getImage() {
        return basicProfile.getImage();
    }

    public String getDescription() {
        return basicProfile.getDescription();
    }

    public String getGithubUrl() {
        return githubProfile.getGithubUrl();
    }

    public String getCompany() {
        return githubProfile.getCompany();
    }

    public String getLocation() {
        return githubProfile.getLocation();
    }

    public String getWebsite() {
        return githubProfile.getWebsite();
    }

    public String getTwitter() {
        return githubProfile.getTwitter();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
