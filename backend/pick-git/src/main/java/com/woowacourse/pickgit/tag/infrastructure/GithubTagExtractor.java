package com.woowacourse.pickgit.tag.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubTagExtractor implements PlatformTagExtractor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<String> extractTags(String accessToken, String userName, String repositoryName) {
        String url = generateApiUrl(userName, repositoryName);
        String response = requestLanguageTags(url, accessToken);
        return parseResponseIntoLanguageTags(response);
    }

    private String generateApiUrl(String userName, String repositoryName) {
        return "https://api.github.com/repos/" + userName + "/" + repositoryName + "/languages";
    }

    private String requestLanguageTags(String url, String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        return new RestTemplate()
            .exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class)
            .getBody();
    }

    private List<String> parseResponseIntoLanguageTags(String response) {
        try {
            Set<String> tags = OBJECT_MAPPER.readValue(response, LinkedHashMap.class)
                .keySet();
            return new ArrayList<>(tags);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException();
        }
    }
}
