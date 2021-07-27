package com.woowacourse.pickgit.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class StarResponseDto {

    @JsonProperty("stargazers_count")
    private int stars;

    private StarResponseDto() {
    }

    public StarResponseDto(int stars) {
        this.stars = stars;
    }

    public int getStars() {
        return stars;
    }
}
