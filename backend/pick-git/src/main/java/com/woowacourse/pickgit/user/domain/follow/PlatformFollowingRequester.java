package com.woowacourse.pickgit.user.domain.follow;

public interface PlatformFollowingRequester {

    void follow(String targetName, String accessToken);
    void unfollow(String targetName, String accessToken);
}
