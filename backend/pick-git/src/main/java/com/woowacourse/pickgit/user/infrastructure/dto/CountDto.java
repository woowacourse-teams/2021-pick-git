package com.woowacourse.pickgit.user.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class CountDto {

    @JsonProperty("total_count")
    private int count;

    private CountDto() {
    }

    public CountDto(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
