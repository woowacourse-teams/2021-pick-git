package com.woowacourse.pickgit.user.domain.follow;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Followings {

    @OneToMany(mappedBy = "source")
    private List<Follow> followings;
}

