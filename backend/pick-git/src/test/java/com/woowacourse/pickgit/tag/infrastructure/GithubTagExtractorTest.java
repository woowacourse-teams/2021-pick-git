package com.woowacourse.pickgit.tag.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GithubTagExtractorTest {

    private static final String TESTER_ACCESS_TOKEN = "oauth.access.token";
    private static final String USER_NAME = "jipark3";
    private static final String REPOSITORY_NAME = "doms-react";

    private PlatformTagExtractor platformTagExtractor;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PlatformApiRequester platformApiRequester = new MockTagApiRequester();
        platformTagExtractor = new GithubTagExtractor(platformApiRequester, objectMapper);
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
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("P0001");
    }

    @DisplayName("username/repositoryname 링크에 해당하는 경로가 존재하지 않으면 조회 예외가 발생한다.")
    @Test
    void extractTags_InvalidUrl_ExceptionThrown() {
        assertThatCode(() -> {
            platformTagExtractor.extractTags(TESTER_ACCESS_TOKEN, "invalidpath", REPOSITORY_NAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("P0001");
    }
}
