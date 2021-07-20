package com.woowacourse.pickgit.user.domain.profile;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BasicProfile {

    @Column(nullable = false, updatable = false)
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

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
