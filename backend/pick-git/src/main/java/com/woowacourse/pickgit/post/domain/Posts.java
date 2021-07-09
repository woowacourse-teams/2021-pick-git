package com.woowacourse.pickgit.post.domain;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Posts {

    @OneToMany(mappedBy = "user")
    private List<Post> posts;
}
