package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;

public interface PlatformContributionCalculator {

    ContributionResponseDto calculate(String username);
}
