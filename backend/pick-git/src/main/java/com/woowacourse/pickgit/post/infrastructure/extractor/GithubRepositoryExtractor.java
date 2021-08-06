package com.woowacourse.pickgit.post.infrastructure.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GithubRepositoryExtractor implements PlatformRepositoryExtractor {

    private static final String API_URL_FORMAT = "https://api.github.com/users/%s/repos?page=%d&per_page=%d";

    private final ObjectMapper objectMapper;
    private final PlatformRepositoryApiRequester platformRepositoryApiRequester;

    public GithubRepositoryExtractor(
        ObjectMapper objectMapper,
        PlatformRepositoryApiRequester platformRepositoryApiRequester
    ) {
        this.objectMapper = objectMapper;
        this.platformRepositoryApiRequester = platformRepositoryApiRequester;
    }

    @Override
    public List<RepositoryNameAndUrl> extract(String token, String username, Pageable pageable) {
        String apiUrl = generateApiUrl(username, pageable);
        String response = platformRepositoryApiRequester.request(token, apiUrl);

        return parseToRepositories(response);
    }

    private String generateApiUrl(String username, Pageable pageable) {
        return String
            .format(API_URL_FORMAT, username, pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    private List<RepositoryNameAndUrl> parseToRepositories(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RepositoryParseException();
        }
    }
}
