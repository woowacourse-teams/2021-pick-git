package com.woowacourse.pickgit.authentication.infrastructure;

import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.infrastructure.dto.OAuthAccessTokenRequest;
import com.woowacourse.pickgit.authentication.infrastructure.dto.OAuthAccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubOAuthClient implements OAuthClient {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.url.redirect}")
    private String redirectUrl;

    @Value("${github.url.access-token}")
    private String accessTokenUrl;

    @Override
    public String getLoginUrl() {
        return "https://github.com/login/oauth/authorize?"
            + "client_id=" + clientId
            + "&redirect_url=" + redirectUrl;
    }

    @Override
    public String getAccessToken(String code) {
        OAuthAccessTokenRequest githubAccessTokenRequest = new OAuthAccessTokenRequest(clientId, clientSecret, code);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(githubAccessTokenRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        String accessToken = restTemplate
            .exchange(accessTokenUrl, HttpMethod.POST, httpEntity, OAuthAccessTokenResponse.class)
            .getBody()
            .getAccessToken();

        if (accessToken == null) {
            throw new IllegalArgumentException("깃헙 인증 에러");
        }
        return accessToken;
    }
}
