package com.woowacourse.pickgit.user.domain.follow;

import java.util.ArrayList;
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
    private List<Follow> followers = new ArrayList<>();

    public Followers() {
    }

    public void add(Follow follow) {
        followers.add(follow);
    }

    public void remove(Follow follow) {
        followers.remove(follow);
    }

    public int count() {
        return followers.size();
    }
}
