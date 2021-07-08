package com.woowacourse.pickgit.post.domain.comment;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Comments {

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}
