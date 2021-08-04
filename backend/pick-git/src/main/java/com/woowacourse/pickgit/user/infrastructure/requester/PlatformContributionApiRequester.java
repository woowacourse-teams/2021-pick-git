package com.woowacourse.pickgit.user.infrastructure.requester;

public interface PlatformContributionApiRequester {

    String request(String url, String accessToken);
}
