package com.woowacourse.pickgit.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class StarsResponseDto {

    @JsonProperty("stargazers_count")
    private int stars;

    private StarsResponseDto() {
    }

    public StarsResponseDto(int stars) {
        this.stars = stars;
    }

    public int getStars() {
        return stars;
    }
}
