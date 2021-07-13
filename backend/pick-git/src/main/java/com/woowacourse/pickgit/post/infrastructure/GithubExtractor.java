package com.woowacourse.pickgit.post.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.post.domain.PlatformExtractor;
import com.woowacourse.pickgit.post.infrastructure.dto.RepositoryResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GithubExtractor implements PlatformExtractor {

    private final ObjectMapper objectMapper;
    private final PlatformApiRequester platformApiRequester;

    public GithubExtractor(
        ObjectMapper objectMapper,
        PlatformApiRequester platformApiRequester) {
        this.objectMapper = objectMapper;
        this.platformApiRequester = platformApiRequester;
    }

    @Override
    public List<RepositoryResponse> showRepositories(String token) {
        String url = "https://api.github.com/user/repos";
        String response = platformApiRequester.repositories(token, url);

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
