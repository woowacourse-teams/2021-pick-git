package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;


public class MockRepositoryApiRequester implements PlatformRepositoryApiRequester {

    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String KEYWORD = "woowa";
    private static final String API_USER_REPO_URL_FORMAT = "https://api.github.com/users/%s/repos?page=1&per_page=50";
    private static final String API_SEARCH_USER_REPO_URL_FORMAT =
        "https://api.github.com/search/repositories?"
            + "q=user:%s %s in:name fork:true&page=%d&per_page=%d";

    @Override
    public String request(String token, String url) {
        String userRepoApiUrl =
            String.format(API_USER_REPO_URL_FORMAT, USERNAME);
        String userRepoSearchApiUrl =
            String.format(
                API_SEARCH_USER_REPO_URL_FORMAT,
                USERNAME,
                KEYWORD,
                1,
                2
            );

        if (isInvalidToken(token)) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 토큰");
        }

        if (isInvalidUrl(url, userRepoApiUrl, userRepoSearchApiUrl)) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 URL");
        }

        if (userRepoApiUrl.equals(url)) {
            return "[{\"name\": \"binghe-hi\", \"html_url\": \"https://github.com/jipark3/binghe-hi\"},"
                + "{\"name\": \"doms-react\", \"html_url\": \"https://github.com/jipark3/doms-react\"}]";
        } else {
            return "[{\"name\": \"woowa-binghe-hi\", \"html_url\": \"https://github.com/jipark3/woowa-binghe-hi\"},"
                + "{\"name\": \"woowa-doms-react\", \"html_url\": \"https://github.com/jipark3/woowa-doms-react\"}]";
        }
    }

    private boolean isInvalidToken(String token) {
        return !token.equals(ACCESS_TOKEN);
    }

    private boolean isInvalidUrl(
        String url,
        String userRepoApiUrl,
        String userRepoSearchApiUrl
    ) {
        return !url.equals(userRepoApiUrl) &&
            !url.equals(userRepoSearchApiUrl);
    }
}
