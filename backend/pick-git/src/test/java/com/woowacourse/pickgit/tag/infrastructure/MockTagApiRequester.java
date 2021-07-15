package com.woowacourse.pickgit.tag.infrastructure;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class MockTagApiRequester implements PlatformApiRequester{

    private static final String TESTER_ACCESS_TOKEN = "oauth.access.token";
    private static final String USER_NAME = "jipark3";
    private static final String REPOSITORY_NAME = "doms-react";

    @Override
    public String requestTags(String url, String accessToken) {
        String validUrl =
            "https://api.github.com/repos/" + USER_NAME + "/" + REPOSITORY_NAME + "/languages";
        if (!accessToken.equals(TESTER_ACCESS_TOKEN)) {
            throw new PlatformHttpErrorException();
        }
        if (!url.equals(validUrl)) {
            throw new PlatformHttpErrorException();
        }
        return "{\"JavaScript\": \"91949\", \"HTML\": \"13\", \"CSS\": \"9\"}";
    }
}
