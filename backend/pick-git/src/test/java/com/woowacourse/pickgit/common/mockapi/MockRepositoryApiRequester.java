package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;

public class MockRepositoryApiRequester implements PlatformRepositoryApiRequester {

    private static final String API_URL_FORMAT = "https://api.github.com/users/%s/repos?page=1&per_page=50";
    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "oauth.access.token";

    @Override
    public String request(String token, String url) {
        String apiUrl = String.format(API_URL_FORMAT, USERNAME);

        if (isInvalidToken(token)) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 토큰");
        }
        if (isInvalidUrl(url, apiUrl)) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 URL");
        }

        return "[{\"name\": \"binghe-hi\", \"html_url\": \"https://github.com/jipark3/binghe-hi\"},"
            + "{\"name\": \"doms-react\", \"html_url\": \"https://github.com/jipark3/doms-react\"}]";
    }

    private boolean isInvalidToken(String token) {
        return !ACCESS_TOKEN.equals(token);
    }

    private boolean isInvalidUrl(String url, String apiUrl) {
        return !url.equals(apiUrl);
    }
}
