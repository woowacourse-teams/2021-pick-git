package com.woowacourse.pickgit.user.domain.contribution;

public interface PlatformContributionCalculator {

    Contribution calculate(String accessToken, String username);
}
