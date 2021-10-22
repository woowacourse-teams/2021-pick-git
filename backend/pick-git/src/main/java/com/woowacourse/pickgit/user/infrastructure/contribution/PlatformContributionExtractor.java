package com.woowacourse.pickgit.user.infrastructure.contribution;

import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import java.util.Map;

public interface PlatformContributionExtractor {

    void extractStars(
        String accessToken,
        String username,
        Map<ContributionCategory, Integer> bucket
    );

    void extractCount(
        ContributionCategory category,
        String restUrl,
        String accessToken,
        String username,
        Map<ContributionCategory, Integer> bucket
    );
}
