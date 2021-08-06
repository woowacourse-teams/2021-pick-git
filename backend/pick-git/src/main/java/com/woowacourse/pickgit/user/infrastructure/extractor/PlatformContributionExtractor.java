package com.woowacourse.pickgit.user.infrastructure.extractor;

import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.ItemDto;

public interface PlatformContributionExtractor {

    ItemDto extractStars(String accessToken, String username);

    CountDto extractCount(String restUrl, String accessToken, String username);
}
