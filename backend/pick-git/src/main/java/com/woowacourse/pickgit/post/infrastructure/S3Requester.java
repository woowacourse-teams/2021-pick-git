package com.woowacourse.pickgit.post.infrastructure;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.infrastructure.S3Storage.StorageDto;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Profile("!test")
@Component
public class S3Requester {

    private final WebClient webClient;

    public S3Requester(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<String> storeImages(String url, MultiValueMap<String, Object> body) {
        return webClient.post()
            .uri(url)
            .bodyValue(body)
            .retrieve()
            .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new PlatformHttpErrorException(errorMessage))))
            .bodyToMono(StorageDto.class)
            .blockOptional()
            .orElseThrow(PlatformHttpErrorException::new)
            .getUrls();
    }
}
