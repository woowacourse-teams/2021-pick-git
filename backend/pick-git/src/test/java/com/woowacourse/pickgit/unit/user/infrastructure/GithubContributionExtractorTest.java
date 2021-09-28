package com.woowacourse.pickgit.unit.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockContributionApiRequester;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.user.ContributionParseException;
import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.ItemDto;
import com.woowacourse.pickgit.user.infrastructure.dto.StarsDto;
import com.woowacourse.pickgit.user.infrastructure.extractor.GithubContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.extractor.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GithubContributionExtractorTest {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String INVALID_ACCESS_TOKEN = "invalid" + ACCESS_TOKEN;
    private static final String USERNAME = "testUser";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrlFormatForStar = "https://api.github.com/search/repositories?q=user:%s stars:>=1";
    private final String apiUrlFormatForCount = "https://api.github.com/search/";

    private PlatformContributionExtractor platformContributionExtractor;

    @BeforeEach
    void setUp() {
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiRequester(),
            "https://api.github.com"
        );
    }

    @DisplayName("스타 개수를 추출한다.")
    @Test
    void extractStars_Stars_Success() {
        // when
        ItemDto stars = platformContributionExtractor.extractStars(ACCESS_TOKEN, USERNAME);

        // then
        assertThat(stars.getItems()
            .stream()
            .mapToInt(StarsDto::getStars)
            .sum())
            .isEqualTo(11);
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 스타 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractStars_InvalidTokenInCaseOfStars_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractStars(INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    @DisplayName("JSON key 값이 다른 경우 스타 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractStars_InvalidJsonKeyInCaseOfStars_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractStars(ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("커밋 개수를 추출한다.")
    @Test
    void extractCount_Commits_Success() {
        // when
        CountDto commits = platformContributionExtractor
            .extractCount("/search/commits?q=committer:%s", ACCESS_TOKEN, USERNAME);

        // then
        assertThat(commits.getCount()).isEqualTo(48);
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 커밋 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidTokenInCaseOfCommits_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/commits?q=committer:%s", INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    @DisplayName("JSON key 값이 다른 경우 커밋 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfCommits_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/commits?q=committer:%s", ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("PR 개수를 추출한다.")
    @Test
    void extractCount_PRs_Success() {
        // when
        CountDto prs = platformContributionExtractor
            .extractCount("/search/issues?q=author:%s type:pr", ACCESS_TOKEN, USERNAME);

        // then
        assertThat(prs.getCount()).isEqualTo(48);
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 PR 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidTokenInCaseOfPRs_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/issues?q=author:%s type:pr", INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    @DisplayName("JSON key 값이 다른 경우 PR 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfPRs_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/issues?q=author:%s type:pr", ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("이슈 개수를 추출한다.")
    @Test
    void extractCount_Issues_Success() {
        // when
        CountDto issues = platformContributionExtractor
            .extractCount("/search/issues?q=author:%s type:issue", ACCESS_TOKEN, USERNAME);

        // then
        assertThat(issues.getCount()).isEqualTo(48);
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 이슈 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidTokenInCaseOfIssues_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/issues?q=author:%s type:issue", INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    @DisplayName("JSON key 값이 다른 경우 이슈 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfIssues_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/issues?q=author:%s type:issue", ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("퍼블릭 레포지토리 개수를 추출한다.")
    @Test
    void extractCount_Repos_Success() {
        // when
        CountDto repos = platformContributionExtractor
            .extractCount("/search/repositories?q=user:%s is:public", ACCESS_TOKEN, USERNAME);

        // then
        assertThat(repos.getCount()).isEqualTo(48);
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 퍼블릭 레포지토리 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidTokenInCaseOfRepos_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/repositories?q=user:%s is:public", INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    @DisplayName("JSON key 값이 다른 경우 퍼블릭 레포지토리 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfRepos_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            "https://api.github.com"
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/search/repositories?q=user:%s is:public", ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    private static class MockContributionApiErrorRequester
        implements PlatformContributionApiRequester {

        @Override
        public String request(String url, String accessToken) {
            if (!ACCESS_TOKEN.equals(accessToken)) {
                throw new PlatformHttpErrorException();
            }

            if (url.contains("stars")) {
                return "{\"items\": [{\"stargazers\": \"5\"}, {\"stargazers\": \"6\"}]}";
            }
            return "{\"total\": \"48\"}";
        }
    }
}
