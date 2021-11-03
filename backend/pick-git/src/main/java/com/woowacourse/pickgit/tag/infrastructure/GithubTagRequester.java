package com.woowacourse.pickgit.tag.infrastructure;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import java.util.LinkedHashMap;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Profile("!test")
@Component
public class GithubTagRequester {

    private final WebClient webClient;

    public GithubTagRequester(WebClient webClient) {
        this.webClient = webClient;
    }

    public Set<String> getTags(String accessToken, String url) {
        return webClient.get()
            .uri(url)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
            .retrieve()
            .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new PlatformHttpErrorException(errorMessage))))
            .bodyToMono(new ParameterizedTypeReference<LinkedHashMap<String, String>>() {})
            .blockOptional()
            .orElseThrow(PlatformHttpErrorException::new)
            .keySet();
    }
}
