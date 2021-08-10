package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;

public class MockPlatformFollowingRequester implements PlatformFollowingRequester {
    private static final String URL_FORMAT = "https://api.github.com/user/following/%s";

    @Override
    public void follow(String targetName, String accessToken) {
    }

    @Override
    public void unfollow(String targetName, String accessToken) {
    }
}
