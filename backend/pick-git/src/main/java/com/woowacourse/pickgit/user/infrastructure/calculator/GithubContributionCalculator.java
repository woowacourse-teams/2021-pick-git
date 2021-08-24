package com.woowacourse.pickgit.user.infrastructure.calculator;

import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
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
        return new Contribution(
            calculateStars(accessToken, username),
            calculateCommits(accessToken, username),
            calculatePRs(accessToken, username),
            calculateIssues(accessToken, username),
            calculateRepos(accessToken, username)
        );
    }

    private int calculateStars(String accessToken, String username) {
        ItemDto stars = platformContributionExtractor.extractStars(accessToken, username);

        return stars.getItems()
            .stream()
            .mapToInt(StarsDto::getStars)
            .sum();
    }

    private int calculateCommits(String accessToken, String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/commits?q=committer:%s", accessToken, username);

        return count.getCount();
    }

    private int calculatePRs(String accessToken, String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/issues?q=author:%s type:pr", accessToken, username);

        return count.getCount();
    }

    private int calculateIssues(String accessToken, String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/issues?q=author:%s type:issue", accessToken, username);

        return count.getCount();
    }

    private int calculateRepos(String accessToken, String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/repositories?q=user:%s is:public", accessToken, username);

        return count.getCount();
    }
}
