package com.woowacourse.pickgit.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class CountResponseDto {

    @JsonProperty("total_count")
    private int count;

    private CountResponseDto() {
    }

    public CountResponseDto(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
