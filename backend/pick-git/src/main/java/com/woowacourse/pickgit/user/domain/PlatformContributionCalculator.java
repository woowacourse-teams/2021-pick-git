package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.domain.dto.ContributionDto;

public interface PlatformContributionCalculator {

    ContributionDto calculate(String username);
}
