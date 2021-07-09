package com.woowacourse.pickgit.tag.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

class GithubTagExtractorTest {

    private static final String TESTER_ACCESS_TOKEN = "valid-token-aaaa";
    private static final String USER_NAME = "jipark3";
    private static final String REPOSITORY_NAME = "doms-react";

    private PlatformTagExtractor platformTagExtractor;

    @BeforeEach
    void setUp() {
        String validUrl =
            "https://api.github.com/repos/" + USER_NAME + "/" + REPOSITORY_NAME + "/languages";

        PlatformApiRequester platformApiRequester = ((url, accessToken) -> {
            if (!url.equals(validUrl)) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }
            if (!accessToken.equals(TESTER_ACCESS_TOKEN)) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            }
            return "{\"JavaScript\": \"91949\", \"HTML\": \"13\", \"CSS\": \"9\"}";
        });

        platformTagExtractor = new GithubTagExtractor(platformApiRequester);
    }

    @DisplayName("명시된 User의 Repository에 기술된 Language Tags를 추출한다.")
    @Test
    void extractTags_ValidRepository_ExtractionSuccess() {
        List<String> tags = platformTagExtractor
            .extractTags(TESTER_ACCESS_TOKEN, USER_NAME, REPOSITORY_NAME);

        assertThat(tags).contains("JavaScript", "HTML", "CSS");
    }

    @DisplayName("토큰이 유효하지 않은 경우 권한 예외가 발생한다.")
    @Test
    void extractTags_InvalidAccessToken_ExceptionThrown() {
        assertThatCode(() -> {
            platformTagExtractor.extractTags("invalidTOken", USER_NAME, REPOSITORY_NAME);
        }).isInstanceOf(HttpClientErrorException.class)
            .hasMessageContaining("401");
    }

    @DisplayName("username/repositoryname 링크에 해당하는 경로가 존재하지 않으면 조회 예외가 발생한다.")
    @Test
    void extractTags_InvalidUrl_ExceptionThrown() {
        assertThatCode(() -> {
            platformTagExtractor.extractTags(TESTER_ACCESS_TOKEN, "invalidpath", REPOSITORY_NAME);
        }).isInstanceOf(HttpClientErrorException.class)
            .hasMessageContaining("404");
    }
}
