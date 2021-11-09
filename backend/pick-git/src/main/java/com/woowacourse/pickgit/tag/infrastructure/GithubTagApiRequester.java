package com.woowacourse.pickgit.tag.infrastructure;

import com.woowacourse.pickgit.common.network.RestTemplateClient;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Profile("!test")
@Component
public class GithubTagApiRequester implements PlatformTagApiRequester {

    @Override
    public String requestTags(String url, String accessToken) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(accessToken);
            RequestEntity<Void> requestEntity = RequestEntity.get(url)
                .headers(httpHeaders)
                .build();
            return new RestTemplateClient().exchange(requestEntity, String.class)
                .getBody();
        } catch (HttpClientErrorException e) {
            throw new PlatformHttpErrorException(e.getMessage());
        }
    }
}
