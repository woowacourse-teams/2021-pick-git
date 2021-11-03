package com.woowacourse.pickgit.tag.infrastructure;

import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GithubTagExtractor implements PlatformTagExtractor {

    private static final String OTHER_TAG = "Other";

    private final GithubTagRequester githubTagRequester;
    private final String apiBaseUrl;

    public GithubTagExtractor(
        GithubTagRequester githubTagRequester,
        @Value("${security.github.url.api}") String apiBaseUrl
    ) {
        this.githubTagRequester = githubTagRequester;
        this.apiBaseUrl = apiBaseUrl;
    }

    public List<String> extractTags(String accessToken, String userName, String repositoryName) {
        String url = generateApiUrl(userName, repositoryName);
        Set<String> tags = githubTagRequester.getTags(accessToken, url);
        tags.remove(OTHER_TAG);
        return new ArrayList<>(tags);
    }

    private String generateApiUrl(String userName, String repositoryName) {
        String url = apiBaseUrl + "/repos/%s/%s/languages";
        return String.format(url, userName, repositoryName);
    }
}
