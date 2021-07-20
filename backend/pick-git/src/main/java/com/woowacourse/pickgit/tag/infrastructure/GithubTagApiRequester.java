package com.woowacourse.pickgit.tag.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("!test")
public class GithubTagApiRequester implements PlatformTagApiRequester {

    @Override
    public String requestTags(String url, String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        RequestEntity<Void> requestEntity = RequestEntity.get(url)
            .headers(httpHeaders)
            .build();
        return new RestTemplate().exchange(requestEntity, String.class)
            .getBody();
    }
}
