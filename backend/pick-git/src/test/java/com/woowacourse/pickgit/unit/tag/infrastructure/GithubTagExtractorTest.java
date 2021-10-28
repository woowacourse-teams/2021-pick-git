package com.woowacourse.pickgit.unit.tag.infrastructure;

import static com.woowacourse.pickgit.common.fixture.TRepository.PICK_GIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockTagApiRequester;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import com.woowacourse.pickgit.tag.infrastructure.GithubTagExtractor;
import com.woowacourse.pickgit.tag.infrastructure.PlatformTagApiRequester;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GithubTagExtractorTest {

    private static final String TESTER_ACCESS_TOKEN = "oauth.access.token";
    private static final String USER_NAME = "jipark3";
    private static final String REPOSITORY_NAME = PICK_GIT.name();

    private PlatformTagExtractor platformTagExtractor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PlatformTagApiRequester platformTagApiRequester = new MockTagApiRequester();
        platformTagExtractor = new GithubTagExtractor(
            platformTagApiRequester,
            objectMapper,
            "https://api.github.com"
        );
    }

    @DisplayName("명시된 User의 Repository에 기술된 Language Tags(Other 제외)를 추출한다.")
    @Test
    void extractTags_ValidRepository_ExtractionSuccess() {
        List<String> tags = platformTagExtractor
            .extractTags(TESTER_ACCESS_TOKEN, USER_NAME, REPOSITORY_NAME);

        assertThat(tags).containsExactlyInAnyOrderElementsOf(PICK_GIT.getTags());
    }

    @DisplayName("토큰이 유효하지 않은 경우 권한 예외가 발생한다.")
    @Test
    void extractTags_InvalidAccessToken_ExceptionThrown() {
        assertThatCode(() -> {
            platformTagExtractor.extractTags("invalidTOken", USER_NAME, REPOSITORY_NAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("요청 URL 링크(username/repositoryname)에 해당하는 경로가 존재하지 않으면 조회 예외가 발생한다.")
    @Test
    void extractTags_InvalidUrl_ExceptionThrown() {
        assertThatCode(() -> {
            platformTagExtractor.extractTags(TESTER_ACCESS_TOKEN, "invalidpath", "invalid");
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }
}
