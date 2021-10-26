package com.woowacourse.pickgit.post.infrastructure.extractor;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryItemDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@Component
public class GithubRepositoryExtractor implements PlatformRepositoryExtractor {

    private final String apiBaseUrl;

    @Autowired
    private WebClient webClient;

    public GithubRepositoryExtractor(
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public List<RepositoryNameAndUrl> extract(String token, String username, Pageable pageable) {
        String apiUrl = generateExtractApiUrl(username, pageable);

        return sendGithubApi(token, apiUrl)
            .bodyToMono(new ParameterizedTypeReference<List<RepositoryNameAndUrl>>() {})
            .blockOptional()
            .orElseThrow(RepositoryParseException::new);
    }

    private String generateExtractApiUrl(String username, Pageable pageable) {
        String format = apiBaseUrl + "/users/%s/repos?page=%d&per_page=%d";
        return String.format(format, username, pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    private ResponseSpec sendGithubApi(String token, String apiUrl) {
        return webClient.get()
            .uri(apiUrl)
            .headers(httpHeaders -> {
                httpHeaders.setBearerAuth(token);
                httpHeaders.set("Accept", "application/vnd.github.v3+json");
            })
            .retrieve()
            .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new PlatformHttpErrorException(errorMessage))));
    }

    @Override
    public List<RepositoryNameAndUrl> search(
        String token,
        String username,
        String keyword,
        Pageable pageable
    ) {
        String url = generateSearchApiUrl(username, keyword, pageable);

        return sendGithubApi(token, url)
            .bodyToMono(RepositoryItemDto.class)
            .blockOptional()
            .orElseThrow(RepositoryParseException::new)
            .getItems();
    }

    private String generateSearchApiUrl(
        String username,
        String keyword,
        Pageable pageable
    ) {
        String format = apiBaseUrl +
            "/search/repositories?q=user:%s %s in:name fork:true&page=%d&per_page=%d";
        return String.format(
            format, username, keyword, pageable.getPageNumber() + 1, pageable.getPageSize()
        );
    }
}
