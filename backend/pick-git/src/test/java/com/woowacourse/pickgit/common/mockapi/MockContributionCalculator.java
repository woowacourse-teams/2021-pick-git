package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.user.domain.contribution.Contribution;
import com.woowacourse.pickgit.user.domain.contribution.ContributionCategory;
import com.woowacourse.pickgit.user.domain.contribution.PlatformContributionCalculator;
import java.util.EnumMap;
import java.util.Map;

public class MockContributionCalculator implements PlatformContributionCalculator {

    @Override
    public Contribution calculate(String accessToken, String username) {
        Map<ContributionCategory, Integer> contributionMap = new EnumMap<>(ContributionCategory.class);
        contributionMap.put(ContributionCategory.STAR, 11);
        for (int i = 1; i < ContributionCategory.values().length; i++) {
            contributionMap.put(ContributionCategory.values()[i], 48);
        }
       return new Contribution(contributionMap);
    }
}
