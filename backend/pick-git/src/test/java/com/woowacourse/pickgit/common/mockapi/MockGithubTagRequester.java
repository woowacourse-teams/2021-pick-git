package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.query.fixture.TRepository;
import com.woowacourse.pickgit.tag.infrastructure.GithubTagRequester;
import java.util.Map;
import java.util.Set;
import org.springframework.web.reactive.function.client.WebClient;

public class MockGithubTagRequester extends GithubTagRequester {

    public MockGithubTagRequester(WebClient webClient) {
        super(webClient);
    }

    private static final String VALID_URL_REGEX = "https:\\/\\/api\\.github\\.com\\/repos\\/.*\\/.*\\/languages";
    private static final String TESTER_ACCESS_TOKEN = "oauth.access.token";

    @Override
    public Set<String> getTags(String accessToken, String url) {
        if (!accessToken.contains(TESTER_ACCESS_TOKEN)) {
            throw new PlatformHttpErrorException();
        }

        if (url.matches(VALID_URL_REGEX) && TRepository.exists(url.split("/")[5])) {
            Map<String, String> tags = TRepository
                .valueOf(url.split("/")[5])
                .getTagsAsJson();
            return tags.keySet();
        }

        throw new PlatformHttpErrorException();
    }
}
