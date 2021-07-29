package com.woowacourse.pickgit.unit.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockContributionApiRequester;
import com.woowacourse.pickgit.exception.user.ContributionParseException;
import com.woowacourse.pickgit.user.domain.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import com.woowacourse.pickgit.user.infrastructure.extractor.GithubContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.requester.PlatformContributionApiRequester;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class GithubContributionExtractorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrlFormatForStar = "https://api.github.com/search/repositories?q=user:%s stars:>=1";
    private final String apiUrlFormatForCount = "https://api.github.com/search/";

    private PlatformContributionExtractor platformContributionExtractor;

    @BeforeEach
    void setUp() {
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );
    }

    @DisplayName("스타 개수를 추출한다.")
    @Test
    void extractStars_Stars_Success() {
        // when
        List<StarResponseDto> stars = platformContributionExtractor.extractStars("testUser");

        // then
        assertThat(stars.stream()
            .mapToInt(StarResponseDto::getStars)
            .sum())
            .isEqualTo(11);
    }

    @DisplayName("JSON key 값이 다른 경우 스타 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractStars_InvalidJsonKeyInCaseOfStars_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractStars("testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("커밋 개수를 추출한다.")
    @Test
    void extractCount_Commits_Success() {
        // when
        CountResponseDto commits =
            platformContributionExtractor.extractCount("/commits?q=committer:%s", "testUser");

        // then
        assertThat(commits.getCount()).isEqualTo(48);
    }

    @DisplayName("JSON key 값이 다른 경우 커밋 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfCommits_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractCount("/commits?q=committer:%s", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("PR 개수를 추출한다.")
    @Test
    void extractCount_PRs_Success() {
        // when
        CountResponseDto prs =
            platformContributionExtractor.extractCount("/issues?q=author:%s type:pr", "testUser");

        // then
        assertThat(prs.getCount()).isEqualTo(48);
    }

    @DisplayName("JSON key 값이 다른 경우 PR 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfPRs_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractCount("/issues?q=author:%s type:pr", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("이슈 개수를 추출한다.")
    @Test
    void extractCount_Issues_Success() {
        // when
        CountResponseDto issues =
            platformContributionExtractor
                .extractCount("/issues?q=author:%s type:issue", "testUser");

        // then
        assertThat(issues.getCount()).isEqualTo(48);
    }

    @DisplayName("JSON key 값이 다른 경우 이슈 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfIssues_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractCount("/issues?q=author:%s type:issue", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    @DisplayName("퍼블릭 레포지토리 개수를 추출한다.")
    @Test
    void extractCount_Repos_Success() {
        // when
        CountResponseDto repos =
            platformContributionExtractor
                .extractCount("/repositories?q=user:%s is:public", "testUser");

        // then
        assertThat(repos.getCount()).isEqualTo(48);
    }

    @DisplayName("JSON key 값이 다른 경우 퍼블릭 레포지토리 개수를 추출할 수 없다. - 500 예외")
    @Test
    void extractCount_InvalidJsonKeyInCaseOfRepos_500Exception() {
        // given
        platformContributionExtractor = new GithubContributionExtractor(
            objectMapper,
            new MockContributionApiErrorRequester(),
            apiUrlFormatForStar,
            apiUrlFormatForCount
        );

        // when
        assertThatThrownBy(() -> {
            platformContributionExtractor.extractCount("/repositories?q=user:%s is:public", "testUser");
        }).isInstanceOf(ContributionParseException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("활동 통계를 조회할 수 없습니다.");
    }

    private static class MockContributionApiErrorRequester
        implements PlatformContributionApiRequester
    {

        @Override
        public String request(String url) {
            if (url.contains("stars")) {
                return "[{\"stargazers\": \"5\"}, {\"stargazers\": \"6\"}]";
            }
            return "{\"total\": \"48\"}";
        }
    }
}
