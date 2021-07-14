package com.woowacourse.pickgit.post.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class MockRepositoryApiRequester implements PlatformRepositoryApiRequester {

    private static final String API_URL_FORMAT = "https://api.github.com/users/%s/repos";
    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "oauth-access-token";

    @Override
    public String request(String token, String url) {
        String apiUrl = String.format(API_URL_FORMAT, USERNAME);

        if (isNotValidToken(token)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        if (isNotValidUrl(url, apiUrl)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
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
