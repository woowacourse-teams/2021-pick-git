package com.woowacourse.pickgit.tag.infrastructure;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubApiRequester implements PlatformApiRequester {

    @Override
    public String requestTags(String url, String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        return new RestTemplate()
            .exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class)
            .getBody();
    }
}
