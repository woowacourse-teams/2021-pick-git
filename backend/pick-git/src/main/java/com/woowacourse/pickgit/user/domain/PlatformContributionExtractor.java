package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.domain.dto.CountResponseDto;
import com.woowacourse.pickgit.user.domain.dto.StarsResponseDto;
import java.util.List;

public interface PlatformContributionExtractor {

    List<StarsResponseDto> extractStars(String username);

    CountResponseDto extractCount(String restUrl, String username);
}
