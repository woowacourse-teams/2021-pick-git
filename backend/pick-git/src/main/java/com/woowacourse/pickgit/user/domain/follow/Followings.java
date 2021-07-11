package com.woowacourse.pickgit.user.domain.follow;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Followings {

    @OneToMany(mappedBy = "source", fetch = FetchType.LAZY)
    private List<Follow> followings = new ArrayList<>();

    public Followings() {
    }

    public int followingCount() {
        return followings.size();
    }
}

