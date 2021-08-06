package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;

public class MockContributionApiRequester implements PlatformContributionApiRequester {

    @Override
    public String request(String url, String accessToken) {
        String validPrefix = "https://api.github.com/search/";

        if (!url.startsWith(validPrefix)) {
            throw new PlatformHttpErrorException();
        }
        if (!"oauth.access.token".equals(accessToken)) {
            throw new PlatformHttpErrorException();
        }

        if (url.contains("stars")) {
            return "{\"items\": [{\"stargazers_count\": \"5\"}, {\"stargazers_count\": \"6\"}]}";
        }
        return "{\"total_count\": \"48\"}";
    }
}
