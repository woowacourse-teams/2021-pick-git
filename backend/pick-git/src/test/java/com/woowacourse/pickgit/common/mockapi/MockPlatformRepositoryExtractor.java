package com.woowacourse.pickgit.common.mockapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import java.util.List;
import org.springframework.data.domain.Pageable;

public class MockPlatformRepositoryExtractor implements PlatformRepositoryExtractor {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String API_USER_REPO_URL_REGEX =
        "https:\\/\\/api\\.github\\.com\\/users\\/.*\\/repos\\?page=[0-9]*&per_page=[0-9]*";
    private static final String API_SEARCH_USER_REPO_URL_REGEX =
        "https:\\/\\/api\\.github\\.com\\/search\\/repositories\\?q=user:.* .* in:name fork:true&page=[0-9]*&per_page=[0-9]*";

    private String apiBaseUrl = "https://api.github.com";

    @Override
    public List<RepositoryNameAndUrl> extract(String token, String username, Pageable pageable) {
        String url = generateApiUrl(username, pageable);
        if (isInvalidToken(token)) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 토큰");
        }

        if (isInvalidUrl(url) || url.contains("invalid")) {
            throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 URL");
        }
        String json = getJson(url);

        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

    private String generateApiUrl(String username, Pageable pageable) {
        String format = apiBaseUrl + "/users/%s/repos?page=%d&per_page=%d";
        return String
            .format(format, username, pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    private boolean isInvalidToken(String token) {
        return !token.contains(ACCESS_TOKEN);
    }

    private boolean isInvalidUrl(String url) {
        return !url.matches(API_USER_REPO_URL_REGEX) &&
            !url.matches(API_SEARCH_USER_REPO_URL_REGEX);
    }

    private String getJson(String url) {
        if (url.matches(API_USER_REPO_URL_REGEX)) {
            return
                "[{\"name\": \"binghe-hi\", \"html_url\": \"https://github.com/jipark3/binghe-hi\"},"
                    + "{\"name\": \"doms-react\", \"html_url\": \"https://github.com/jipark3/doms-react\"}]";
        }
        return
            "{\"items\": [{\"name\": \"woowa-binghe-hi\", \"html_url\": \"https://github.com/jipark3/woowa-binghe-hi\"},"
                + "{\"name\": \"woowa-doms-react\", \"html_url\": \"https://github.com/jipark3/woowa-doms-react\"}]}";
    }

    @Override
    public List<RepositoryNameAndUrl> search(String token, String username, String keyword, Pageable pageable) {
        return extract(token,  keyword, pageable);
    }
}
