package com.woowacourse.pickgit.unit.post.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockRepositoryApiRequester;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
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
        List<RepositoryNameAndUrl> repositories = platformRepositoryExtractor
            .extract(ACCESS_TOKEN, "jipark3", 0L, 50L);

        assertThat(repositories)
            .usingRecursiveComparison()
            .isEqualTo(List.of(
                createRepositoryResponseDto("binghe-hi", "https://github.com/jipark3/binghe-hi"),
                createRepositoryResponseDto("doms-react", "https://github.com/jipark3/doms-react")
            ));
    }

    @DisplayName("토큰이 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidAccessToken_401Exception() {
        // then
        assertThatThrownBy(() -> {
            platformRepositoryExtractor.extract(ACCESS_TOKEN + "hi", USERNAME, 0L, 50L);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    @DisplayName("사용자가 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidUserName_404Exception() {
        // then
        assertThatThrownBy(() -> {
            platformRepositoryExtractor.extract(ACCESS_TOKEN, USERNAME + "hi", 0L, 50L);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }

    private RepositoryNameAndUrl createRepositoryResponseDto(String name, String url) {
        return new RepositoryNameAndUrl(name, url);
    }
}
