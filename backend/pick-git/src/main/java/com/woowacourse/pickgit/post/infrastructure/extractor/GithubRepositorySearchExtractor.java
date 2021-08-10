package com.woowacourse.pickgit.post.infrastructure.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositorySearchExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GithubRepositorySearchExtractor implements PlatformRepositorySearchExtractor {

    private static final String API_URL_FORMAT =
        "https://api.github.com/search/repositories?"
            + "q=user:%s %s in:name fork:true&page=%d&per_page=%d";

    private final ObjectMapper objectMapper;
    private final PlatformRepositoryApiRequester platformRepositoryApiRequester;

    public GithubRepositorySearchExtractor(
        ObjectMapper objectMapper,
        PlatformRepositoryApiRequester platformRepositoryApiRequester
    ) {
        this.objectMapper = objectMapper;
        this.platformRepositoryApiRequester = platformRepositoryApiRequester;
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

        return parseToRepositories(response);
    }

    private String generateApiUrl(
        String username,
        String keyword,
        int page,
        int limit
    ) {

        return String.format(
            API_URL_FORMAT,
            username,
            keyword,
            page,
            limit
        );
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
