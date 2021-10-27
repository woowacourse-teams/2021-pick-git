package com.woowacourse.pickgit.post.infrastructure.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositorySearchExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryItemDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GithubRepositorySearchExtractor implements PlatformRepositorySearchExtractor {

    private final ObjectMapper objectMapper;
    private final PlatformRepositoryApiRequester platformRepositoryApiRequester;
    private final String apiBaseUrl;

    public GithubRepositorySearchExtractor(
        ObjectMapper objectMapper,
        PlatformRepositoryApiRequester platformRepositoryApiRequester,
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.objectMapper = objectMapper;
        this.platformRepositoryApiRequester = platformRepositoryApiRequester;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public List<RepositoryNameAndUrl> extract(
        String token,
        String username,
        String keyword,
        Pageable pageable
    ) {
        String response = platformRepositoryApiRequester.request(
            token,
            generateApiUrl(username, keyword, pageable)
        );

        return parseToRepositories(response).getItems();
    }

    private String generateApiUrl(
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

    private RepositoryItemDto parseToRepositories(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RepositoryParseException();
        }
    }
}
