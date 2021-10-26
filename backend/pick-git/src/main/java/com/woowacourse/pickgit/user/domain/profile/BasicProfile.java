package com.woowacourse.pickgit.user.domain.profile;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BasicProfile {

    @Column(unique = true, nullable = false, updatable = false)
    private String name;

    private String image;

    private String description;

    protected BasicProfile() {
    }

    public BasicProfile(String name, String image, String description) {
        this.name = name;
        this.image = image;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
