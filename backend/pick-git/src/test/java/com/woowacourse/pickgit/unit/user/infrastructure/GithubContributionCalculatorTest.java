package com.woowacourse.pickgit.unit.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.common.mockapi.MockContributionApiRequester;
import com.woowacourse.pickgit.exception.user.ContributionParseException;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.domain.dto.ContributionDto;
import com.woowacourse.pickgit.user.infrastructure.calculator.GithubContributionCalculator;
import com.woowacourse.pickgit.user.infrastructure.extractor.GithubContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GithubContributionCalculatorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrlFormatForStar = "https://api.github.com/search/repositories?q=user:%s stars:>=1";
    private final String apiUrlFormatForCount = "https://api.github.com/search/";

    private PlatformContributionExtractor platformContributionExtractor;
    private PlatformContributionCalculator platformContributionCalculator;

    @BeforeEach
    void setUp() {
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );
    }

    @DisplayName("활동 통계를 조회할 수 있다.")
    @Test
    void calculate_Valid_Success() {
        // given
        ContributionDto contribution = UserFactory.mockContributionDto();

        // when
        ContributionDto result = platformContributionCalculator.calculate("testUser");

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(contribution);
    }

    @DisplayName("스타 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidStars_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractStars("testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("커밋 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidCommits_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractCount("/commits?q=committer:%s", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("PR 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidPRs_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractCount("/issues?q=author:%s type:pr", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("이슈 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidIssues_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/issues?q=author:%s type:issue", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("퍼블릭 레포지토리 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidRepos_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor
                .extractCount("/repositories?q=user:%s is:public", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    private static class MockContributionApiErrorRequester
        implements PlatformContributionApiRequester {

        @Override
        public String request(String url) {
            if (url.contains("stars")) {
                return "[{\"stargazers\": \"5\"}, {\"stargazers\": \"6\"}]";
            }
            return "{\"total\": \"48\"}";
        }
    }
}
