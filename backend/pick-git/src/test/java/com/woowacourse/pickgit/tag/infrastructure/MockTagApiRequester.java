package com.woowacourse.pickgit.tag.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class MockTagApiRequester implements PlatformApiRequester{

    private static final String TESTER_ACCESS_TOKEN = "valid-token-aaaa";
    private static final String USER_NAME = "jipark3";
    private static final String REPOSITORY_NAME = "doms-react";

    @Override
    public String requestTags(String url, String accessToken) {
        String validUrl =
            "https://api.github.com/repos/" + USER_NAME + "/" + REPOSITORY_NAME + "/languages";
        if (!accessToken.equals(TESTER_ACCESS_TOKEN)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        if (!url.equals(validUrl)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return "{\"JavaScript\": \"91949\", \"HTML\": \"13\", \"CSS\": \"9\"}";
    }
}
