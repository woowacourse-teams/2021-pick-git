package com.woowacourse.pickgit.user.infrastructure.calculator;

import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.infrastructure.dto.ItemDto;
import com.woowacourse.pickgit.user.infrastructure.dto.StarsDto;
import com.woowacourse.pickgit.user.infrastructure.extractor.PlatformContributionExtractor;
import org.springframework.stereotype.Component;

@Component
public class GithubContributionCalculator implements PlatformContributionCalculator {

    private final PlatformContributionExtractor platformContributionExtractor;

    public GithubContributionCalculator(
        PlatformContributionExtractor platformContributionExtractor
    ) {
        this.platformContributionExtractor = platformContributionExtractor;
    }

    @Override
    public Contribution calculate(String accessToken, String username) {
        return Contribution.builder()
            .starsCount(calculateStars(accessToken, username))
            .commitsCount(counts("/search/commits?q=committer:%s", accessToken, username))
            .prsCount(counts("/search/issues?q=author:%s type:pr", accessToken, username))
            .issuesCount(counts("/search/issues?q=author:%s type:issue", accessToken, username))
            .reposCount(counts("/search/issues?q=author:%s type:issue", accessToken, username))
            .build();
    }

    private int calculateStars(String accessToken, String username) {
        ItemDto stars = platformContributionExtractor.extractStars(accessToken, username);

        return stars.getItems()
            .stream()
            .mapToInt(StarsDto::getStars)
            .sum();
    }

    private int counts(String url, String accessToken, String username) {
        return platformContributionExtractor
            .extractCount(url, accessToken, username)
            .getCount();
    }
}
