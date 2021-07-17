package com.woowacourse.pickgit.post.presentation.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;

public class HomeFeedRequest {

    private AppUser appUser;
    private Long page;
    private Long limit;

    public HomeFeedRequest(AppUser appUser, Long page, Long limit) {
        this.appUser = appUser;
        this.page = page;
        this.limit = limit;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public int getPage() {
        return Math.toIntExact(page);
    }

    public int getLimit() {
        return Math.toIntExact(limit);
    }
}
