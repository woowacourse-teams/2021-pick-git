package com.woowacourse.pickgit.common.mockapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.common.fixture.TRepository;
import com.woowacourse.pickgit.tag.infrastructure.PlatformTagApiRequester;
import java.util.Map;

public class MockTagApiRequester implements PlatformTagApiRequester {

    private static final String VALID_URL_REGEX = "https:\\/\\/api\\.github\\.com\\/repos\\/.*\\/.*\\/languages";
    private static final String TESTER_ACCESS_TOKEN = "oauth.access.token";

    @Override
    public String requestTags(String url, String accessToken) {
        try {
            if (!accessToken.contains(TESTER_ACCESS_TOKEN)) {
                throw new PlatformHttpErrorException();
            }

            if (url.matches(VALID_URL_REGEX) && TRepository.exists(url.split("/")[5])) {
                Map<String, String> tags = TRepository
                    .valueOf(url.split("/")[5])
                    .getTagsAsJson();
                return new ObjectMapper().writeValueAsString(tags);
            }

            throw new PlatformHttpErrorException();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }
}
