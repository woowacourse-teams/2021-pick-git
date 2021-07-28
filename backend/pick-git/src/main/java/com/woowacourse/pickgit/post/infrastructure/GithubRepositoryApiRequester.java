package com.woowacourse.pickgit.post.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class GithubRepositoryApiRequester implements PlatformRepositoryApiRequester {

    private final RestClient restClient;

    public GithubRepositoryApiRequester(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String request(String token, String url) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        RequestEntity<Void> requestEntity = RequestEntity
            .get(url)
            .headers(httpHeaders)
            .build();

        return restClient
            .exchange(requestEntity, String.class)
            .getBody();
    }
}
