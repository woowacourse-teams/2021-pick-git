package com.woowacourse.pickgit.unit.post.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockRepositoryApiRequester;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryUrlAndName;
import com.woowacourse.pickgit.post.infrastructure.extractor.GithubRepositoryExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GithubRepositoryExtractorTest {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String USERNAME = "jipark3";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PlatformRepositoryExtractor platformRepositoryExtractor;

    @BeforeEach
    void setUp() {
        platformRepositoryExtractor =
            new GithubRepositoryExtractor(objectMapper, new MockRepositoryApiRequester());
    }

    @DisplayName("깃허브 레포지토리를 요청하면, 레포지토리를 반환한다.")
    @Test
    void extract_requestGithubRepository_returnRepositories() {
        List<RepositoryUrlAndName> repositories = platformRepositoryExtractor
            .extract(ACCESS_TOKEN, "jipark3");

        assertThat(repositories)
            .usingRecursiveComparison()
            .isEqualTo(List.of(
                createRRepositoryResponseDto("binghe-hi", null),
                createRRepositoryResponseDto("doms-react", null)
            ));
    }

    @DisplayName("토큰이 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidAccessToken_401Exception() {
        // then
        assertThatThrownBy(() -> {
            platformRepositoryExtractor.extract(ACCESS_TOKEN + "hi", USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("사용자가 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidUserName_404Exception() {
        // then
        assertThatThrownBy(() -> {
            platformRepositoryExtractor.extract(ACCESS_TOKEN, USERNAME + "hi");
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    private RepositoryUrlAndName createRRepositoryResponseDto(String name, String url) {
        return new RepositoryUrlAndName(name, url);
    }
}
