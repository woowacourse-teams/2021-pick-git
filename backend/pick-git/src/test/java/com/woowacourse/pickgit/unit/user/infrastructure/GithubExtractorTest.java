package com.woowacourse.pickgit.unit.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockApiRequester;
import com.woowacourse.pickgit.user.domain.PlatformExtractor;
import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import com.woowacourse.pickgit.user.infrastructure.extractor.GithubExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GithubExtractorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PlatformExtractor platformExtractor;

    @BeforeEach
    void setUp() {
        platformExtractor = new GithubExtractor(objectMapper, new MockApiRequester());
    }

    @DisplayName("스타 개수를 추출한다.")
    @Test
    void extractStars_Stars_Success() {
        // when
        List<StarResponseDto> stars = platformExtractor.extractStars("testUser");

        // then
        assertThat(stars.stream()
            .mapToInt(StarResponseDto::getStars)
            .sum())
            .isEqualTo(11);
    }

    @DisplayName("커밋 개수를 추출한다.")
    @Test
    void extractCount_Commits_Success() {
        // when
        CountResponseDto commits =
            platformExtractor.extractCount("/commits?q=committer:%s", "testUser");

        // then
        assertThat(commits.getCount()).isEqualTo(48);
    }

    @DisplayName("PR 개수를 추출한다.")
    @Test
    void extractCount_PRs_Success() {
        // when
        CountResponseDto prs =
            platformExtractor.extractCount("/issues?q=author:%s type:pr", "testUser");

        // then
        assertThat(prs.getCount()).isEqualTo(48);
    }

    @DisplayName("이슈 개수를 추출한다.")
    @Test
    void extractCount_Issues_Success() {
        // when
        CountResponseDto issues =
            platformExtractor.extractCount("/issues?q=author:%s type:issue", "testUser");

        // then
        assertThat(issues.getCount()).isEqualTo(48);
    }

    @DisplayName("퍼블릭 레포지토리 개수를 추출한다.")
    @Test
    void extractCount_Repos_Success() {
        // when
        CountResponseDto repos =
            platformExtractor.extractCount("/repositories?q=user:%s is:public", "testUser");

        // then
        assertThat(repos.getCount()).isEqualTo(48);
    }
}
