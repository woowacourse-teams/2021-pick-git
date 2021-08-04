package com.woowacourse.pickgit.tag.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class GithubTagExtractor implements PlatformTagExtractor {

    private static final String GITHUB_TAG_API_FORMAT
        = "https://api.github.com/repos/%s/%s/languages";

    private final PlatformTagApiRequester platformTagApiRequester;
    private final ObjectMapper objectMapper;

    public GithubTagExtractor(PlatformTagApiRequester platformTagApiRequester,
        ObjectMapper objectMapper) {
        this.platformTagApiRequester = platformTagApiRequester;
        this.objectMapper = objectMapper;
    }

    public List<String> extractTags(String accessToken, String userName, String repositoryName) {
        String url = generateApiUrl(userName, repositoryName);
        String response = platformTagApiRequester.requestTags(url, accessToken);
        return parseResponseIntoLanguageTags(response);
    }

    private String generateApiUrl(String userName, String repositoryName) {
        return String.format(GITHUB_TAG_API_FORMAT, userName, repositoryName);
    }

    private List<String> parseResponseIntoLanguageTags(String response) {
        try {
            Set<String> tags = objectMapper
                .readValue(response, new TypeReference<LinkedHashMap<String, String>>() {
                })
                .keySet();
            return new ArrayList<>(tags);
        } catch (JsonProcessingException e) {
            throw new PlatformHttpErrorException();
        }
    }
}
