package com.woowacourse.pickgit.user.infrastructure.requester;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("!test")
public class GithubFollowingRequester implements PlatformFollowingRequester {
    private static final String URL_FORMAT = "https://api.github.com/user/following/%s";

    @Override
    public void follow(String targetName, String accessToken) {
        requestFollowingOrUnFollowing(HttpMethod.PUT, targetName, accessToken);
    }

    @Override
    public void unfollow(String targetName, String accessToken) {
        requestFollowingOrUnFollowing(HttpMethod.DELETE, targetName, accessToken);
    }

    private void requestFollowingOrUnFollowing(HttpMethod method, String targetName, String accessToken) {
        String url = String.format(URL_FORMAT, targetName);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(accessToken);
            httpHeaders.set("Accept", "application/vnd.github.v3+json");

            RequestEntity<Void> requestEntity = RequestEntity
                .method(method, url)
                .headers(httpHeaders)
                .build();

            new RestTemplate()
                .exchange(requestEntity, Void.class);
        } catch (HttpClientErrorException e) {
            throw new PlatformHttpErrorException(e.getMessage());
        }
    }
}
