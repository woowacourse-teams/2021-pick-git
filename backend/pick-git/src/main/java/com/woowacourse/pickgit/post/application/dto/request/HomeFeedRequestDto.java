package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;

@Builder
public class HomeFeedRequestDto {

    private String requestUserName;
    private boolean isGuest;
    private Long page;
    private Long limit;

    public HomeFeedRequestDto(AppUser appUser, Long page, Long limit) {
        if(appUser.isGuest()) {
            requestUserName = null;
        } else {
            requestUserName = appUser.getUsername();
        }

        this.isGuest = appUser.isGuest();
        this.page = page;
        this.limit = limit;
    }

    public HomeFeedRequestDto(String requestUserName, boolean isGuest, Long page, Long limit) {
        this.requestUserName = requestUserName;
        this.isGuest = isGuest;
        this.page = page;
        this.limit = limit;
    }

    public String getRequestUserName() {
        return requestUserName;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public Long getPage() {
        return page;
    }

    public Long getLimit() {
        return limit;
    }
}
