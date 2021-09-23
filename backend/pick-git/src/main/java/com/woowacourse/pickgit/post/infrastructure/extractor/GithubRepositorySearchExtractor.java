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
import org.springframework.stereotype.Component;

@Component
public class GithubRepositorySearchExtractor implements PlatformRepositorySearchExtractor {

    private final ObjectMapper objectMapper;
    private final PlatformRepositoryApiRequester platformRepositoryApiRequester;
    private final String apiUrlFormatForSearchRepository;

    public GithubRepositorySearchExtractor(
        ObjectMapper objectMapper,
        PlatformRepositoryApiRequester platformRepositoryApiRequester,
        @Value("${github.repository.search.format-url}") String apiUrlFormatForSearchRepository
    ) {
        this.objectMapper = objectMapper;
        this.platformRepositoryApiRequester = platformRepositoryApiRequester;
        this.apiUrlFormatForSearchRepository = apiUrlFormatForSearchRepository;
    }

    @Override
    public List<RepositoryNameAndUrl> extract(
        String token,
        String username,
        String keyword,
        int page,
        int limit
    ) {
        String response =
            platformRepositoryApiRequester.request(
                token,
                generateApiUrl(username, keyword, page + 1, limit)
        );

        return parseToRepositories(response).getItems();
    }

    private String generateApiUrl(
        String username,
        String keyword,
        int page,
        int limit
    ) {

        return String.format(
            apiUrlFormatForSearchRepository,
            username,
            keyword,
            page,
            limit
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
