package com.woowacourse.pickgit.post.application.dto.request;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public class HomeFeedRequestDto {

    private String requestUserName;
    private boolean isGuest;
    private Pageable pageable;
    private Long lastPostId;

    private HomeFeedRequestDto() {
    }

    public HomeFeedRequestDto(AppUser appUser, Pageable pageable) {
        if(appUser.isGuest()) {
            requestUserName = null;
        } else {
            requestUserName = appUser.getUsername();
        }

        this.isGuest = appUser.isGuest();
        this.pageable = pageable;
    }

    public HomeFeedRequestDto(AppUser appUser, Pageable pageable, Long lastPostId) {
        if(appUser.isGuest()) {
            requestUserName = null;
        } else {
            requestUserName = appUser.getUsername();
        }

        this.isGuest = appUser.isGuest();
        this.pageable = pageable;
        this.lastPostId = lastPostId;
    }

    public HomeFeedRequestDto(String requestUserName, boolean isGuest, Pageable pageable) {
        this.requestUserName = requestUserName;
        this.isGuest = isGuest;
        this.pageable = pageable;
    }

    public String getRequestUserName() {
        return requestUserName;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public Long getLastPostId() {
        return lastPostId;
    }
}
