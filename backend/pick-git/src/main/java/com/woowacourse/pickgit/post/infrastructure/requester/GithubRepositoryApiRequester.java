package com.woowacourse.pickgit.post.infrastructure.requester;

import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.post.domain.util.RestClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class GithubRepositoryApiRequester implements PlatformRepositoryApiRequester {

    private final RestClient restClient;

    public GithubRepositoryApiRequester(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String request(String token, String url) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.set("Accept", "application/vnd.github.v3+json");

        RequestEntity<Void> requestEntity = RequestEntity
            .get(url)
            .headers(httpHeaders)
            .build();

        return restClient
            .exchange(requestEntity, String.class)
            .getBody();
    }
}
