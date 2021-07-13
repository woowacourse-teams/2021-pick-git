package com.woowacourse.pickgit.post.infrastructure;

import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubRepositoryApiRequester implements PlatformRepositoryApiRequester {

    @Override
    public String request(String token, String url) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        RequestEntity<Void> requestEntity = RequestEntity
            .get(url)
            .headers(httpHeaders)
            .build();

        return new RestTemplate()
            .exchange(requestEntity, String.class)
            .getBody();
    }
}
