package com.woowacourse.pickgit.unit.post.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockRepositoryApiRequester;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryApiRequester;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositoryExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.post.infrastructure.extractor.GithubRepositoryExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

class GithubRepositoryExtractorTest {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String USERNAME = "jipark3";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PlatformRepositoryExtractor platformRepositoryExtractor;

    @BeforeEach
    void setUp() {
        platformRepositoryExtractor = new GithubRepositoryExtractor(
            objectMapper,
            new MockRepositoryApiRequester(),
            "https://api.github.com"
        );
    }

    @DisplayName("요청 페이지에 퍼블릭 레포지토리가 있는 경우 퍼블릭 레포지토리 목록을 반환한다.")
    @Test
    void extract_requestGithubRepository_returnRepositories() {
        Pageable pageable = PageRequest.of(0, 50);

        List<RepositoryNameAndUrl> repositories = platformRepositoryExtractor
            .extract(ACCESS_TOKEN, USERNAME, pageable);

        assertThat(repositories)
            .usingRecursiveComparison()
            .isEqualTo(List.of(
                createRepositoryResponseDto("binghe-hi", "https://github.com/jipark3/binghe-hi"),
                createRepositoryResponseDto("doms-react", "https://github.com/jipark3/doms-react")
            ));
    }

    @DisplayName("요청 페이지에 퍼블릭 레포지토리가 없는 경우 빈 배열을 반환한다.")
    @Test
    void extract_requestGithubRepository_returnEmptyRepositories() {
        // given
        Pageable pageable = PageRequest.of(59, 50);

        platformRepositoryExtractor = new GithubRepositoryExtractor(
            objectMapper,
            new MockEmptyRepositoryApiRequester(),
            "https://api.github.com"
        );

        // when
        List<RepositoryNameAndUrl> repositories = platformRepositoryExtractor
            .extract(ACCESS_TOKEN, USERNAME, pageable);

        // then
        assertThat(repositories).isEqualTo(List.of());
    }

    @DisplayName("토큰이 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidToken_500Exception() {
        // given
        Pageable pageable = PageRequest.of(0, 50);

        // then
        assertThatThrownBy(() -> {
            platformRepositoryExtractor.extract("invalid token", USERNAME, pageable);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("유효하지 않은 외부 플랫폼 토큰");
    }

    @DisplayName("사용자가 유효하지 않은 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidUsername_500Exception() {
        // given
        Pageable pageable = PageRequest.of(0, 50);

        // then
        assertThatThrownBy(() -> {
            platformRepositoryExtractor.extract(ACCESS_TOKEN, "invalid", pageable);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("유효하지 않은 외부 플랫폼 URL");
    }

    private RepositoryNameAndUrl createRepositoryResponseDto(String name, String url) {
        return new RepositoryNameAndUrl(name, url);
    }

    private static class MockEmptyRepositoryApiRequester implements PlatformRepositoryApiRequester {

        private static final String API_URL_FORMAT = "https://api.github.com/users/%s/repos?page=60&per_page=50";
        private static final String USERNAME = "jipark3";
        private static final String ACCESS_TOKEN = "oauth.access.token";

        @Override
        public String request(String token, String url) {
            String apiUrl = String.format(API_URL_FORMAT, USERNAME);

            if (isInvalidToken(token)) {
                throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 토큰");
            }
            if (isInvalidUrl(url, apiUrl)) {
                throw new PlatformHttpErrorException("유효하지 않은 외부 플랫폼 URL");
            }

            return "[]";
        }

        private boolean isInvalidToken(String token) {
            return !ACCESS_TOKEN.equals(token);
        }

        private boolean isInvalidUrl(String url, String apiUrl) {
            return !url.equals(apiUrl);
        }
    }
}
