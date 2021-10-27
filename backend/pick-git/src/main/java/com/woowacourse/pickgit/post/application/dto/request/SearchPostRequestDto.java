package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;

public class SearchPostRequestDto {

    private Long id;
    private String userName;
    private boolean isGuest;

    private SearchPostRequestDto() {
    }

    public SearchPostRequestDto(Long id, AppUser appUser) {
        this(
            id,
            appUser.isGuest() ? null : appUser.getUsername(),
            appUser.isGuest()
        );
    }

    public SearchPostRequestDto(Long id, String userName, boolean isGuest) {
        this.id = id;
        this.userName = userName;
        this.isGuest = isGuest;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isGuest() {
        return isGuest;
    }
}
