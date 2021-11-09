package com.woowacourse.pickgit.user.infrastructure.requester;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Profile("!test")
@Component
public class GithubFollowingRequester implements PlatformFollowingRequester {

    private final String apiBaseUrl;

    public GithubFollowingRequester(
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public void follow(String targetName, String accessToken) {
        requestFollowingOrUnFollowing(HttpMethod.PUT, targetName, accessToken);
    }

    @Override
    public void unfollow(String targetName, String accessToken) {
        requestFollowingOrUnFollowing(HttpMethod.DELETE, targetName, accessToken);
    }

    private void requestFollowingOrUnFollowing(
        HttpMethod method,
        String targetName,
        String accessToken
    ) {
        try {
            String format = apiBaseUrl + "/user/following/%s";
            String url = String.format(format, targetName);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(accessToken);
            httpHeaders.set("Accept", "application/vnd.github.v3+json");

            RequestEntity<Void> requestEntity = RequestEntity
                .method(method, url)
                .headers(httpHeaders)
                .build();

            new RestTemplate().exchange(requestEntity, Void.class);
        } catch (HttpClientErrorException e) {
            throw new PlatformHttpErrorException(e.getMessage());
        }
    }
}
