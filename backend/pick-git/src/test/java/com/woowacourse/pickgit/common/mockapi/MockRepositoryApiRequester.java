package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;


public class MockRepositoryApiRequester implements PlatformRepositoryApiRequester {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String API_USER_REPO_URL_REGEX =
        "https:\\/\\/api\\.github\\.com\\/users\\/.*\\/repos\\?page=[0-9]*&per_page=[0-9]*";
    private static final String API_SEARCH_USER_REPO_URL_REGEX =
        "https:\\/\\/api\\.github\\.com\\/search\\/repositories\\?q=user:.* .* in:name fork:true&page=[0-9]*&per_page=[0-9]*";

    @Override
    public String request(String token, String url) {
        String[] split = token.split("\\.");
        String username = split[split.length - 1];

         if (isInvalidToken(token)) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 토큰");
        }

        if (isInvalidUrl(url) || url.contains("invalid")) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 URL");
        }

        if (url.matches(API_USER_REPO_URL_REGEX)) {
            return
                "[{\"name\": \"binghe-hi\", \"html_url\": \"https://github.com/jipark3/binghe-hi\"},"
                    + "{\"name\": \"doms-react\", \"html_url\": \"https://github.com/jipark3/doms-react\"}]";
        } else {
            return
                "{\"items\": [{\"name\": \"woowa-binghe-hi\", \"html_url\": \"https://github.com/jipark3/woowa-binghe-hi\"},"
                    + "{\"name\": \"woowa-doms-react\", \"html_url\": \"https://github.com/jipark3/woowa-doms-react\"}]}";
        }
    }

    private boolean isInvalidToken(String token) {
        return !token.contains(ACCESS_TOKEN);
    }

    private boolean isInvalidUrl(String url) {
        return !url.matches(API_USER_REPO_URL_REGEX) &&
            !url.matches(API_SEARCH_USER_REPO_URL_REGEX);
    }
}
