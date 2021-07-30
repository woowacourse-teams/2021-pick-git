package com.woowacourse.pickgit.user.domain;

import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.StarsDto;
import java.util.List;

public interface PlatformContributionExtractor {

    List<StarsDto> extractStars(String username);

    CountDto extractCount(String restUrl, String username);
}
