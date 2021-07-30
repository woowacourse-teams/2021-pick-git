package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.Posts;
import com.woowacourse.pickgit.post.domain.comment.Comment;
import com.woowacourse.pickgit.user.domain.follow.Follow;
import com.woowacourse.pickgit.user.domain.follow.Followers;
import com.woowacourse.pickgit.user.domain.follow.Followings;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
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
    private Followers followers = new Followers();

    @Embedded
    private Followings followings = new Followings();

    @Embedded
    private Posts posts = new Posts();

    protected User() {
    }

    public User(
        Long id,
        BasicProfile basicProfile,
        GithubProfile githubProfile) {
        this.id = id;
        this.basicProfile = basicProfile;
        this.githubProfile = githubProfile;
    }

    public User(
        BasicProfile basicProfile,
        GithubProfile githubProfile) {
        this.basicProfile = basicProfile;
        this.githubProfile = githubProfile;
    }

    public void changeGithubProfile(GithubProfile githubProfile) {
        this.githubProfile = githubProfile;
    }

    public void updateDescription(String description) {
        this.basicProfile.setDescription(description);
    }

    public void updateProfileImage(String imageUrl) {
        this.basicProfile.setImage(imageUrl);
    }

    public void follow(User target) {
        Follow follow = new Follow(this, target);

        if (this.followings.existFollow(follow)) {
            throw new DuplicateFollowException();
        }
        this.followings.add(follow);
        target.followers.add(follow);
    }

    public void unfollow(User target) {
        Follow follow = new Follow(this, target);

        if (!this.followings.existFollow(follow)) {
            throw new InvalidFollowException();
        }
        this.followings.remove(follow);
        target.followers.remove(follow);
    }

    public void addComment(Post post, Comment comment) {
        comment.writeBy(this);
        post.addComment(comment);
    }

    public Boolean isFollowing(User targetUser) {
        return this.followings.isFollowing(targetUser);
    }

    public int getFollowerCount() {
        return followers.count();
    }

    public int getFollowingCount() {
        return followings.count();
    }

    public int getPostCount() {
        return posts.getCounts();
    }

    public Long getId() {
        return this.id;
    }

    public BasicProfile getBasicProfile() {
        return basicProfile;
    }

    public GithubProfile getGithubProfile() {
        return githubProfile;
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
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
