package com.woowacourse.pickgit.authentication.infrastructure;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.infrastructure.dto.OAuthAccessTokenRequest;
import com.woowacourse.pickgit.authentication.infrastructure.dto.OAuthAccessTokenResponse;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("!test")
public class GithubOAuthClient implements OAuthClient {

    @Value("${security.github.client.id}")
    private String clientId;

    @Value("${security.github.client.secret}")
    private String clientSecret;

    @Value("${security.github.client.scope}")
    private String scope;

    @Value("${security.github.url.redirect}")
    private String redirectUrl;

    @Value("${security.github.url.access-token}")
    private String accessTokenUrl;

    @Value("${security.github.url.oauth-login-format")
    private String oauthLoginUrlFormat;

    @Value("${security.github.url.user-profile}")
    private String githubProfileUrl;

    @Override
    public String getLoginUrl() {
        return String.format(oauthLoginUrlFormat, clientId, redirectUrl, scope);
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
            throw new PlatformHttpErrorException();
        }
        return accessToken;
    }

    @Override
    public OAuthProfileResponse getGithubProfile(String githubAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Bearer " + githubAccessToken);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate
            .exchange(githubProfileUrl, HttpMethod.GET, httpEntity, OAuthProfileResponse.class)
            .getBody();
    }
}
