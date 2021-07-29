package com.woowacourse.pickgit.user.infrastructure.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.user.domain.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GithubContributionExtractor implements PlatformContributionExtractor {

    private static final String API_URL_FORMAT_FOR_COUNT = "https://api.github.com/search/";
    private static final String API_URL_FORMAT_FOR_STAR = "https://api.github.com/search/repositories?q=user:%s stars:>=1";

    private final ObjectMapper objectMapper;
    private final PlatformContributionApiRequester platformContributionApiRequester;

    public GithubContributionExtractor(
        ObjectMapper objectMapper,
        PlatformContributionApiRequester platformContributionApiRequester
    ) {
        this.objectMapper = objectMapper;
        this.platformContributionApiRequester = platformContributionApiRequester;
    }

    @Override
    public List<StarResponseDto> extractStars(String username) {
        String apiUrl = generateUrl(username);
        String response = platformContributionApiRequester.request(apiUrl);

        return parseToStars(response);
    }

    private String generateUrl(String username) {
        return String.format(API_URL_FORMAT_FOR_STAR, username);
    }

    private List<StarResponseDto> parseToStars(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public CountResponseDto extractCount(String restUrl, String username) {
        String apiUrl = generateUrl(restUrl, username);
        String response = platformContributionApiRequester.request(apiUrl);

        return parseToCount(response);
    }

    private String generateUrl(String restUrl, String username) {
        return API_URL_FORMAT_FOR_COUNT + String.format(restUrl, username);
    }

    private CountResponseDto parseToCount(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }
}
