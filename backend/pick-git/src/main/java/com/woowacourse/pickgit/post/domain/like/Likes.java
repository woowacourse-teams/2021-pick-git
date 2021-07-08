package com.woowacourse.pickgit.post.domain.like;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Likes {

    @OneToMany(mappedBy = "post")
    private List<Like> likes;
}
