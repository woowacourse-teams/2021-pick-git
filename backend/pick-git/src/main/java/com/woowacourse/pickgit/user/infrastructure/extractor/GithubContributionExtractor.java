package com.woowacourse.pickgit.user.infrastructure.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.user.ContributionParseException;
import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.ItemDto;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GithubContributionExtractor implements PlatformContributionExtractor {

    private final ObjectMapper objectMapper;
    private final PlatformContributionApiRequester platformContributionApiRequester;
    private final String apiBaseUrl;

    public GithubContributionExtractor(
        ObjectMapper objectMapper,
        PlatformContributionApiRequester platformContributionApiRequester,
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.objectMapper = objectMapper;
        this.platformContributionApiRequester = platformContributionApiRequester;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public ItemDto extractStars(String accessToken, String username) {
        String apiUrl = generateUrl(username);
        String response = platformContributionApiRequester.request(apiUrl, accessToken);

        return parseToStars(response);
    }

    private String generateUrl(String username) {
        String url = apiBaseUrl + "/search/repositories?q=user:%s stars:>=1";
        return String.format(url, username);
    }

    private ItemDto parseToStars(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new ContributionParseException();
        }
    }

    @Override
    public CountDto extractCount(String restUrl, String accessToken, String username) {
        String apiUrl = generateUrl(restUrl, username);
        String response = platformContributionApiRequester.request(apiUrl, accessToken);

        return parseToCount(response);
    }

    private String generateUrl(String restUrl, String username) {
        return apiBaseUrl + String.format(restUrl, username);
    }

    private CountDto parseToCount(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new ContributionParseException();
        }
    }
}
