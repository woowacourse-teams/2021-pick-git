package com.woowacourse.pickgit.tag.domain;

import com.woowacourse.pickgit.exception.post.TagFormatException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Tag {

    private static final int MAX_TAG_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = MAX_TAG_LENGTH)
    private String name;

    protected Tag() {
    }

    public Tag(String name) {
        if (isNotValidTag(name)) {
            throw new TagFormatException();
        }
        this.name = name.toLowerCase();
    }

    private boolean isNotValidTag(String name) {
        return Objects.isNull(name)
            || name.isBlank()
            || name.length() > MAX_TAG_LENGTH;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
