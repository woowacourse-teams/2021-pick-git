package com.woowacourse.pickgit.authentication.infrastructure;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.infrastructure.dto.OAuthAccessTokenRequest;
import com.woowacourse.pickgit.authentication.infrastructure.dto.OAuthAccessTokenResponse;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Profile("!test")
@Component
public class GithubOAuthClient implements OAuthClient {

    private static final String OAUTH_LOGIN_URL_SUFFIX =
        "/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=user:follow";
    private static final String ACCESS_TOKEN_URL_SUFFIX =
        "/login/oauth/access_token";

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final String oauthBaseUrl;
    private final String apiBaseUrl;

    public GithubOAuthClient(
        WebClient webClient,
        @Value("${security.github.client.id}") String clientId,
        @Value("${security.github.client.secret}") String clientSecret,
        @Value("${security.github.url.redirect}") String redirectUrl,
        @Value("${security.github.url.oauth}") String oauthBaseUrl,
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.oauthBaseUrl = oauthBaseUrl;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public String getLoginUrl() {
        String oauthLoginUrlFormat = oauthBaseUrl + OAUTH_LOGIN_URL_SUFFIX;
        return String.format(oauthLoginUrlFormat, clientId, redirectUrl);
    }

    @Override
    public String getAccessToken(String code) {
        OAuthAccessTokenRequest githubAccessTokenRequest =
            new OAuthAccessTokenRequest(clientId, clientSecret, code);
        String accessTokenUrl = oauthBaseUrl + ACCESS_TOKEN_URL_SUFFIX;

        return webClient.post()
            .uri(accessTokenUrl)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(githubAccessTokenRequest)
            .retrieve()
            .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new PlatformHttpErrorException(errorMessage))))
            .bodyToMono(OAuthAccessTokenResponse.class)
            .blockOptional()
            .orElseThrow(PlatformHttpErrorException::new)
            .getAccessToken();
    }

    @Override
    public OAuthProfileResponse getGithubProfile(String githubAccessToken) {
        String url = apiBaseUrl + "/user";

        return webClient.get()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
                httpHeaders.setBearerAuth(githubAccessToken);
            })
            .retrieve()
            .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new PlatformHttpErrorException(errorMessage))))
            .bodyToMono(OAuthProfileResponse.class)
            .blockOptional()
            .orElseThrow(PlatformHttpErrorException::new);
    }
}
