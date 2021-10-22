package com.woowacourse.pickgit.user.infrastructure.contribution;

import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.COMMIT;
import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.ISSUE;
import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.PR;
import static com.woowacourse.pickgit.user.domain.contribution.ContributionCategory.REPO;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.exception.platform.PlatformInternalThreadException;
import com.woowacourse.pickgit.user.domain.contribution.Contribution;
import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import com.woowacourse.pickgit.user.domain.contribution.PlatformContributionCalculator;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
public class GithubContributionCalculator implements PlatformContributionCalculator {

    private static final int CONTRIBUTION_COUNT = 5;
    private final PlatformContributionExtractor platformContributionExtractor;

    public GithubContributionCalculator(
        PlatformContributionExtractor platformContributionExtractor
    ) {
        this.platformContributionExtractor = platformContributionExtractor;
    }

    @Override
    public Contribution calculate(String accessToken, String username) {

        CountDownLatch latch = new CountDownLatch(CONTRIBUTION_COUNT);
        Map<ContributionCategory, Integer> bucket = getContributionsViaPlatform(accessToken, username, latch);
        return new Contribution(bucket);
    }

    private Map<ContributionCategory, Integer> getContributionsViaPlatform(String accessToken, String username, CountDownLatch latch) {
        try {
            Map<ContributionCategory, Integer> bucket = new EnumMap<>(ContributionCategory.class);
            platformContributionExtractor.extractStars(accessToken, username, bucket, latch);
            platformContributionExtractor.extractCount(COMMIT, "/search/commits?q=committer:%s", accessToken, username, bucket, latch);
            platformContributionExtractor.extractCount(PR, "/search/issues?q=author:%s type:pr", accessToken, username, bucket ,latch);
            platformContributionExtractor.extractCount(ISSUE, "/search/issues?q=author:%s type:issue", accessToken, username, bucket, latch);
            platformContributionExtractor.extractCount(REPO, "/search/issues?q=author:%s type:issue", accessToken, username, bucket, latch);

            if(!latch.await(2, TimeUnit.SECONDS)) {
                throw new PlatformHttpErrorException();
            }
            return bucket;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlatformInternalThreadException();
        }
    }
}
