package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.infrastructure.PlatformRepositoryApiRequester;

public class MockRepositoryApiRequester implements PlatformRepositoryApiRequester {

    private static final String API_URL_FORMAT = "https://api.github.com/users/%s/repos";
    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "oauth.access.token";

    @Override
    public String request(String token, String url) {
        String apiUrl = String.format(API_URL_FORMAT, USERNAME);

        if (isNotValidToken(token)) {
            throw new PlatformHttpErrorException("외부 플랫폼 토큰 인증 실패");
        }
        if (isNotValidUrl(url, apiUrl)) {
            throw new PlatformHttpErrorException("외부 플랫폼 URL NotFound");
        }

        return "[{\"name\": \"binghe-hi\" }, {\"name\": \"doms-react\" }]";
    }

    private boolean isNotValidToken(String token) {
        return !token.equals(ACCESS_TOKEN);
    }

    private boolean isNotValidUrl(String url, String apiUrl) {
        return !url.equals(apiUrl);
    }
}
