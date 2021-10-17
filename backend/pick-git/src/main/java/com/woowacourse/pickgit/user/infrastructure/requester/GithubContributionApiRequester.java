package com.woowacourse.pickgit.user.infrastructure.requester;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Profile("!test")
@Component
public class GithubContributionApiRequester implements PlatformContributionApiRequester {

    @Override
    public String request(String url, String accessToken) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(accessToken);
            httpHeaders.set("Accept", "application/vnd.github.cloak-preview");

            RequestEntity<Void> requestEntity = RequestEntity
                .get(url)
                .headers(httpHeaders)
                .build();

            return new RestTemplate()
                .exchange(requestEntity, String.class)
                .getBody();
        } catch (HttpClientErrorException e) {
            throw new PlatformHttpErrorException(e.getMessage());
        }
    }
}
