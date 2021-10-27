package com.woowacourse.pickgit.unit.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.domain.contribution.Contribution;
import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import com.woowacourse.pickgit.user.domain.contribution.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.infrastructure.contribution.GithubContributionCalculator;
import com.woowacourse.pickgit.user.infrastructure.contribution.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.ItemDto;
import com.woowacourse.pickgit.user.infrastructure.dto.StarsDto;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GithubContributionCalculatorTest {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String INVALID_ACCESS_TOKEN = "invalid" + ACCESS_TOKEN;
    private static final String USERNAME = "testUser";

    private PlatformContributionExtractor platformContributionExtractor;
    private PlatformContributionCalculator platformContributionCalculator;

    @BeforeEach
    void setUp() {
        platformContributionExtractor = new MockContributionExtractor();
        platformContributionCalculator = new GithubContributionCalculator(
            platformContributionExtractor
        );
    }

    @DisplayName("활동 통계를 조회할 수 있다.")
    @Test
    void calculate_ValidCalculation_Success() {
        // given
        Map<ContributionCategory, Integer> contributionMap = new EnumMap<>(ContributionCategory.class);
        contributionMap.put(ContributionCategory.STAR, 11);
        for (int i = 1; i < ContributionCategory.values().length; i++) {
            contributionMap.put(ContributionCategory.values()[i], 48);
        }
        Contribution contribution = new Contribution(contributionMap);

        // when
        Contribution result = platformContributionCalculator.calculate(ACCESS_TOKEN, USERNAME);

        // then
        assertThat(contribution).usingRecursiveComparison()
            .isEqualTo(result);
    }

    @DisplayName("유효하지 않은 OAuth 토큰인 경우 커밋, PR, 이슈, 퍼블릭 레포지토리 개수를 조회할 수 없다. - 500 예외")
    @Test
    void calculate_InvalidTokenInCaseOfCount_500Exception() {
        // given, when, then
        assertThatThrownBy(() -> {
            platformContributionCalculator.calculate(INVALID_ACCESS_TOKEN, USERNAME);
        }).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue("errorCode", "V0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR)
            .hasMessage("외부 플랫폼 연동에 실패");
    }

    private static class MockContributionExtractor implements PlatformContributionExtractor {

        @Override
        public void extractStars(
            String accessToken,
            String username,
            Map<ContributionCategory, Integer> bucket,
            CountDownLatch countDownLatch
        ) {
            if (!ACCESS_TOKEN.equals(accessToken)) {
                throw new PlatformHttpErrorException();
            }
            List<StarsDto> starsDtos = List.of(new StarsDto(5), new StarsDto(6));
            ItemDto itemDto = new ItemDto(starsDtos);
            bucket.put(ContributionCategory.STAR, itemDto.sum());
            countDownLatch.countDown();
        }

        @Override
        public void extractCount(
            ContributionCategory category,
            String restUrl,
            String accessToken,
            String username,
            Map<ContributionCategory, Integer> bucket,
            CountDownLatch latch
        ) {
            if (!ACCESS_TOKEN.equals(accessToken)) {
                throw new PlatformHttpErrorException();
            }
            CountDto countDto = new CountDto(48);
            bucket.put(category, countDto.getCount());
            latch.countDown();
        }
    }
}
