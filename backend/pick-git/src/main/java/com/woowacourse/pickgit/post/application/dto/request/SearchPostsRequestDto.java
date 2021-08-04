package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;

@Builder
public class SearchPostsRequestDto {

    private String type;
    private String keyword;
    private int page;
    private int limit;
    private String userName;
    private boolean isGuest;

    private SearchPostsRequestDto() {
    }

    public SearchPostsRequestDto(
        String type,
        String keyword,
        int page,
        int limit,
        AppUser appUser
    ) {
        this.type = type;
        this.keyword = keyword;
        this.page = page;
        this.limit = limit;
        this.userName = appUser.isGuest() ? null : appUser.getUsername();
        this.isGuest = appUser.isGuest();
    }

    public SearchPostsRequestDto(
        String type,
        String keyword,
        int page,
        int limit,
        String userName,
        boolean isGuest
    ) {
        this.type = type;
        this.keyword = keyword;
        this.page = page;
        this.limit = limit;
        this.userName = userName;
        this.isGuest = isGuest;
    }

    public String getType() {
        return type;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isGuest() {
        return isGuest;
    }
}
