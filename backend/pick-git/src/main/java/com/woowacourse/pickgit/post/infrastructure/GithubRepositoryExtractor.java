package com.woowacourse.pickgit.post.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GithubRepositoryExtractor implements PlatformRepositoryExtractor {

    private final ObjectMapper objectMapper;
    private final PlatformRepositoryApiRequester platformRepositoryApiRequester;

    public GithubRepositoryExtractor(
        ObjectMapper objectMapper,
        PlatformRepositoryApiRequester platformRepositoryApiRequester) {
        this.objectMapper = objectMapper;
        this.platformRepositoryApiRequester = platformRepositoryApiRequester;
    }

    @Override
    public List<RepositoryResponse> extract(String token) {
        String url = "https://api.github.com/user/repos";
        String response = platformRepositoryApiRequester.request(token, url);

        return parseToRepositories(response);
    }

    private List<RepositoryResponse> parseToRepositories(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }
}
