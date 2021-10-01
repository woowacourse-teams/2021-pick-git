package com.woowacourse.pickgit.post.domain.like;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
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
    private List<Like> likes;

    public Likes() {
        this(new ArrayList<>());
    }

    public Likes(List<Like> likes) {
        this.likes = likes;
    }

    public int getCounts() {
        return likes.size();
    }

    public boolean contains(Like like) {
        return likes.stream()
            .anyMatch(like::equals);
    }

    public void add(Like like) {
        if (likes.contains(like)) {
            throw new DuplicatedLikeException();
        }
        likes.add(like);
    }

    public void remove(Like like) {
        if (!likes.contains(like)) {
            throw new CannotUnlikeException();
        }
        likes.remove(like);
    }

    public List<User> getLikeUsers() {
        return likes.stream()
            .map(Like::getUser)
            .collect(toList());
    }

    public List<Like> getLikes() {
        return likes;
    }
}
