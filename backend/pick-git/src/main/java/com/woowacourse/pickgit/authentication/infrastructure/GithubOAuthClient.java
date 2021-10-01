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

    private static final String OAUTH_LOGIN_URL_SUFFIX =
        "/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=user:follow";
    private static final String ACCESS_TOKEN_URL_SUFFIX =
        "/login/oauth/access_token";

    @Value("${security.github.client.id}")
    private String clientId;

    @Value("${security.github.client.secret}")
    private String clientSecret;

    @Value("${security.github.url.redirect}")
    private String redirectUrl;

    @Value("${security.github.url.oauth}")
    private String oauthBaseUrl;

    @Value("${security.github.url.api}")
    private String apiBaseUrl;

    @Override
    public String getLoginUrl() {
        String oauthLoginUrlFormat = oauthBaseUrl + OAUTH_LOGIN_URL_SUFFIX;
        return String.format(oauthLoginUrlFormat, clientId, redirectUrl);
    }

    @Override
    public String getAccessToken(String code) {
        OAuthAccessTokenRequest githubAccessTokenRequest =
            new OAuthAccessTokenRequest(clientId, clientSecret, code);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(githubAccessTokenRequest, headers);

        String accessTokenUrl = oauthBaseUrl + ACCESS_TOKEN_URL_SUFFIX;
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
        String url = apiBaseUrl + "/user";
        return new RestTemplate()
            .exchange(url, HttpMethod.GET, httpEntity, OAuthProfileResponse.class)
            .getBody();
    }
}
