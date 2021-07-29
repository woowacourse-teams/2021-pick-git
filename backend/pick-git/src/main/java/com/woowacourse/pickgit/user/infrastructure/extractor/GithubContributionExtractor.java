package com.woowacourse.pickgit.user.infrastructure.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.user.domain.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GithubContributionExtractor implements PlatformContributionExtractor {

    private final ObjectMapper objectMapper;
    private final PlatformContributionApiRequester platformContributionApiRequester;
    private final String apiUrlFormatForStar;
    private final String apiUrlFormatForCount;

    public GithubContributionExtractor(
        ObjectMapper objectMapper,
        PlatformContributionApiRequester platformContributionApiRequester,
        @Value("${github.contribution.star-url}") String apiUrlFormatForStar,
        @Value("${github.contribution.count-url}") String apiUrlFormatForCount
    ) {
        this.objectMapper = objectMapper;
        this.platformContributionApiRequester = platformContributionApiRequester;
        this.apiUrlFormatForStar = apiUrlFormatForStar;
        this.apiUrlFormatForCount = apiUrlFormatForCount;
    }

    @Override
    public List<StarResponseDto> extractStars(String username) {
        String apiUrl = generateUrl(username);
        String response = platformContributionApiRequester.request(apiUrl);

        return parseToStars(response);
    }

    private String generateUrl(String username) {
        return String.format(apiUrlFormatForStar, username);
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
        return apiUrlFormatForCount + String.format(restUrl, username);
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
