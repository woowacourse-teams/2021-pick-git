package com.woowacourse.pickgit.user.domain.follow;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Followers {

    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
    private List<Follow> followers = new ArrayList<>();

    public Followers() {
    }

    public int followerCount() {
        return followers.size();
    }
}
