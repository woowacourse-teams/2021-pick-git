package com.woowacourse.pickgit.tag.domain;

import com.woowacourse.pickgit.post.domain.PostTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Tag {

    private static final int MAX_TAG_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @OneToMany(mappedBy = "tag")
    private List<PostTag> postTags = new ArrayList<>();

    protected Tag() {
    }

    public Tag(String name) {
        if (isNotValidTag(name)) {
            throw new TagFormatException();
        }
        this.name = name;
    }

    private boolean isNotValidTag(String name) {
        return Objects.isNull(name)
            || name.isEmpty()
            || name.length() > MAX_TAG_LENGTH;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(getName(), tag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
