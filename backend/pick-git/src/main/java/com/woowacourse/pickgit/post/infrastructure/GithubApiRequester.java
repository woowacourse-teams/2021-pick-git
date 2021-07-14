package com.woowacourse.pickgit.post.infrastructure;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubApiRequester implements PlatformApiRequester {

    @Override
    public String repositories(String token, String url) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        return new RestTemplate()
            .exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class)
            .getBody();
    }
}