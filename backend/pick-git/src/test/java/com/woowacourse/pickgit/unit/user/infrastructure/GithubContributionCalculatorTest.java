package com.woowacourse.pickgit.unit.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockContributionApiRequester;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.user.ContributionParseException;
import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.calculator.GithubContributionCalculator;
import com.woowacourse.pickgit.user.infrastructure.extractor.GithubContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GithubContributionCalculatorTest {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String INVALID_ACCESS_TOKEN = "invalid" + ACCESS_TOKEN;
    private static final String USERNAME = "testUser";

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
    void calculate_ValidCalculation_Success() {
        // given
        Contribution contribution = new Contribution(11, 48, 48, 48, 48);

        // when
        Contribution result = platformContributionCalculator.calculate(ACCESS_TOKEN, USERNAME);

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(contribution);
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 스타 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidTokenInCaseOfStars_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockStarsContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionCalculator.calculate(INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    @DisplayName("JSON key 값이 다른 경우 스타 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidCalculationInCaseOfStars_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockStarsContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionCalculator.calculate(ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    private static class MockStarsContributionApiErrorRequester
        implements PlatformContributionApiRequester {

        @Override
        public String request(String url, String accessToken) {
            if (!ACCESS_TOKEN.equals(accessToken)) {
                throw new PlatformHttpErrorException();
            }

            if (url.contains("stars")) {
                return "{\"items\": [{\"stargazers\": \"5\"}, {\"stargazers\": \"6\"}]}";
            }
            return "{\"total_count\": \"48\"}";
        }
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 커밋, PR, 이슈, 퍼블릭 레포지토리 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidTokenInCaseOfCount_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockCountContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionCalculator.calculate(INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    @DisplayName("JSON key 값이 다른 경우 커밋, PR, 이슈, 퍼블릭 레포지토리 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidCalculationInCaseOfCount_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockCountContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionCalculator.calculate(ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    private static class MockCountContributionApiErrorRequester
        implements PlatformContributionApiRequester {

        @Override
        public String request(String url, String accessToken) {
            if (!ACCESS_TOKEN.equals(accessToken)) {
                throw new PlatformHttpErrorException();
            }

            if (url.contains("stars")) {
                return "{\"items\": [{\"stargazers_count\": \"5\"}, {\"stargazers_count\": \"6\"}]}";
            }
            return "{\"total\": \"48\"}";
        }
    }
}
