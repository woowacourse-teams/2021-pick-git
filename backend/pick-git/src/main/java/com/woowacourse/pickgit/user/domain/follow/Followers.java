package com.woowacourse.pickgit.user.domain.follow;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Followers {

    @OneToMany(mappedBy = "target")
    private List<Follow> followers;
}
