package com.woowacourse.pickgit.user.infrastructure.contribution;

import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public interface PlatformContributionExtractor {

    void extractStars(
        String accessToken,
        String username,
        Map<ContributionCategory, Integer> bucket,
        CountDownLatch latch
    );

    void extractCount(
        ContributionCategory category,
        String restUrl,
        String accessToken,
        String username,
        Map<ContributionCategory, Integer> bucket,
        CountDownLatch latch
    );
}
