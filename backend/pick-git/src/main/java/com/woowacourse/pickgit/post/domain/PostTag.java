package com.woowacourse.pickgit.post.domain;

import com.woowacourse.pickgit.tag.domain.Tag;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.lang.NonNull;

@Entity
public class PostTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @NonNull
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @NonNull
    private Tag tag;

    protected PostTag() {
    }

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostTag postTag = (PostTag) o;
        return Objects.equals(id, postTag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
