package com.woowacourse.pickgit.post.domain;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Posts {

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts;

    protected Posts() {
    }

    public Posts(List<Post> posts) {
        this.posts = posts;
    }

    public int count() {
        return posts.size();
    }
}
