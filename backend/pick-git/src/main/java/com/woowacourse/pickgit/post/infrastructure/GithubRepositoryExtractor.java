package com.woowacourse.pickgit.post.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.post.RepositoryParseException;
import com.woowacourse.pickgit.post.domain.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GithubRepositoryExtractor implements PlatformRepositoryExtractor {

    private static final String API_URL_FORMAT = "https://api.github.com/users/%s/repos";

    private final ObjectMapper objectMapper;
    private final PlatformRepositoryApiRequester platformRepositoryApiRequester;

    public GithubRepositoryExtractor(
        ObjectMapper objectMapper,
        PlatformRepositoryApiRequester platformRepositoryApiRequester) {
        this.objectMapper = objectMapper;
        this.platformRepositoryApiRequester = platformRepositoryApiRequester;
    }

    @Override
    public List<RepositoryResponseDto> extract(String token, String username) {
        String apiUrl = generateApiUrl(username);
        String response = platformRepositoryApiRequester.request(token, apiUrl);

        return parseToRepositories(response);
    }

    private String generateApiUrl(String username) {
        return String.format(API_URL_FORMAT, username);
    }

    private List<RepositoryResponseDto> parseToRepositories(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RepositoryParseException(
                "V0001",
                HttpStatus.BAD_REQUEST,
                "레포지토리 목록을 불러올 수 없습니다."
            );
        }
    }
}
