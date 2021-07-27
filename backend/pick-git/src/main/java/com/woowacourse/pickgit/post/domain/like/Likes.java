package com.woowacourse.pickgit.post.domain.like;

import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Likes {

    @OneToMany(
        mappedBy = "post",
        fetch = FetchType.LAZY,
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Like> likes = new ArrayList<>();

    public Likes() {
        this(new ArrayList<>());
    }

    public Likes(List<Like> likes) {
        this.likes = likes;
    }

    public int getCounts() {
        return likes.size();
    }

    public boolean contains(String userName) {
        return likes.stream()
            .anyMatch(like -> like.isOwnedBy(userName));
    }

    public void add(Like like) {
        likes.add(like);
    }

    public void remove(Like like) {
        likes.remove(like);
    }
}
