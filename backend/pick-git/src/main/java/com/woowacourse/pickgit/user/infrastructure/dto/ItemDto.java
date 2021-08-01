package com.woowacourse.pickgit.user.infrastructure.dto;

import java.util.List;

public class ItemDto {

    private List<StarsDto> items;

    private ItemDto() {
    }

    public ItemDto(List<StarsDto> items) {
        this.items = items;
    }

    public List<StarsDto> getItems() {
        return items;
    }
}
