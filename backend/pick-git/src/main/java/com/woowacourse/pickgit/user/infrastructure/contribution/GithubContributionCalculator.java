package com.woowacourse.pickgit.user.infrastructure.contribution;

import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.COMMIT;
import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.ISSUE;
import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.PR;
import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.REPO;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.user.domain.contribution.Contribution;
import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import com.woowacourse.pickgit.user.domain.contribution.PlatformContributionCalculator;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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
        Map<ContributionCategory, Integer> bucket = new EnumMap<>(ContributionCategory.class);
        platformContributionExtractor.extractStars(accessToken, username, bucket);
        platformContributionExtractor.extractCount(COMMIT, "/search/commits?q=committer:%s", accessToken, username, bucket);
        platformContributionExtractor.extractCount(PR, "/search/issues?q=author:%s type:pr", accessToken, username, bucket);
        platformContributionExtractor.extractCount(ISSUE, "/search/issues?q=author:%s type:issue", accessToken, username, bucket);
        platformContributionExtractor.extractCount(REPO, "/search/issues?q=author:%s type:issue", accessToken, username, bucket);
        waitBusy(bucket);
        return new Contribution(bucket);
    }

    private void waitBusy(Map<ContributionCategory, Integer> bucket) {
        int categoryCounts = ContributionCategory.values().length;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        while (bucket.keySet().size() != categoryCounts) {
            if (stopWatch.getTotalTimeSeconds() >= 2) {
                throw new PlatformHttpErrorException();
            }
        }
        stopWatch.stop();
    }
}
