package com.woowacourse.pickgit.user.infrastructure.requester;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.domain.follow.PlatformFollowingRequester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Profile("!test")
@Component
public class GithubFollowingRequester implements PlatformFollowingRequester {

    private final WebClient webClient;
    private final String apiBaseUrl;

    public GithubFollowingRequester(
        WebClient webClient,
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.webClient = webClient;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public void follow(String targetName, String accessToken) {
        requestFollowingOrUnFollowing(HttpMethod.PUT, targetName, accessToken);
    }

    @Override
    public void unfollow(String targetName, String accessToken) {
        requestFollowingOrUnFollowing(HttpMethod.DELETE, targetName, accessToken);
    }

    private void requestFollowingOrUnFollowing(
        HttpMethod method,
        String targetName,
        String accessToken
    ) {
        String format = apiBaseUrl + "/user/following/%s";
        String url = String.format(format, targetName);

        webClient.method(method)
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.setBearerAuth(accessToken);
                httpHeaders.set("Accept", "application/vnd.github.v3+json");
            })
            .retrieve()
            .onStatus(HttpStatus::isError, clientResponse -> Mono.error(PlatformHttpErrorException::new))
            .bodyToMono(Void.class)
            .block();
    }
}
