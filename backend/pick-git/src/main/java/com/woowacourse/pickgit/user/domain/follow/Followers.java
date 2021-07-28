package com.woowacourse.pickgit.user.domain.follow;

import com.woowacourse.pickgit.exception.user.DuplicateFollowException;
import com.woowacourse.pickgit.exception.user.InvalidFollowException;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Followers {

    @OneToMany(
        mappedBy = "target",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Follow> followers;

    protected Followers() {
    }

    public Followers(List<Follow> followers) {
        this.followers = followers;
    }

    public void add(Follow follow) {
        if (this.followers.contains(follow)) {
            throw new DuplicateFollowException();
        }
        followers.add(follow);
    }

    public void remove(Follow follow) {
        if (!this.followers.contains(follow)) {
            throw new InvalidFollowException();
        }
        followers.remove(follow);
    }

    public int count() {
        return followers.size();
    }
}
