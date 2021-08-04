package com.woowacourse.pickgit.user.domain;

public interface PlatformContributionCalculator {

    Contribution calculate(String accessToken, String username);
}
