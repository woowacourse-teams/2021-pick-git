package com.woowacourse.pickgit.user.domain.follow;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Followings {

    @OneToMany(mappedBy = "source", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();

    public Followings() {
    }

    public boolean existFollow(Follow follow) {
        return this.followings.contains(follow);
    }

    public int count() {
        return followings.size();
    }

    public void add(Follow follow) {
        followings.add(follow);
    }

    public void remove(Follow follow) {
        followings.remove(follow);
    }
}

