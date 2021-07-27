package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformApiRequester;

public class MockApiRequester implements PlatformApiRequester {

    @Override
    public String request(String url) {
        String validPrefix = "https://api.github.com/search/";

        if (!url.startsWith(validPrefix)) {
            throw new PlatformHttpErrorException();
        }

        if (url.contains("stars")) {
            return "[{\"stargazers_count\": \"5\"}, {\"stargazers_count\": \"6\"}]";
        }
        return "{\"total_count\": \"48\"}";
    }
}
