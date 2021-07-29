package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarResponseDto;
import java.util.List;

public interface PlatformContributionExtractor {

    List<StarResponseDto> extractStars(String username);

    CountResponseDto extractCount(String restUrl, String username);
}
