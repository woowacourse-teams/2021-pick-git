package com.woowacourse.pickgit.post.domain;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Posts {

    @OneToMany(mappedBy = "member")
    private List<Post> posts;
}
