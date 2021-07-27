package com.woowacourse.pickgit.post.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Posts {

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts;

    public Posts() {
        this(new ArrayList<>());
    }

    public Posts(List<Post> post) {
        this.posts = post;
    }

    public int count() {
        return posts.size();
    }
}
