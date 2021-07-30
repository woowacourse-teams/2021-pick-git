package com.woowacourse.pickgit.user.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class StarsDto {

    @JsonProperty("stargazers_count")
    private int stars;

    private StarsDto() {
    }

    public StarsDto(int stars) {
        this.stars = stars;
    }

    public int getStars() {
        return stars;
    }
}
